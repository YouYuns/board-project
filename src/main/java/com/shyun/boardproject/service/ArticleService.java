package com.shyun.boardproject.service;

import com.shyun.boardproject.domain.Article;
import com.shyun.boardproject.domain.Hashtag;
import com.shyun.boardproject.domain.UserAccount;
import com.shyun.boardproject.domain.type.SearchType;
import com.shyun.boardproject.dto.ArticleDto;
import com.shyun.boardproject.dto.ArticleWithCommentsDto;
import com.shyun.boardproject.dto.HashtagDto;
import com.shyun.boardproject.repository.ArticleRepository;
import com.shyun.boardproject.repository.HashtagRepository;
import com.shyun.boardproject.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {
    private final HashtagService hashtagService;
    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;
    private final HashtagRepository hashtagRepository;



    //검색어로 게시글 검색
    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        if(searchKeyword == null || searchKeyword.isBlank()){
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }
        return switch (searchType){
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
            default -> articleRepository.findAll(pageable).map(ArticleDto::from);
        };
    }

    //게시글 상세조회 - 댓글까지 같이 조회
    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticleWithComments(Long articleId){
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from)
                .orElseThrow(()-> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));

    }
    // 게시글 상세조회
    @Transactional(readOnly = true)
    public ArticleDto getArticle(Long articleId){
        return articleRepository.findById(articleId)
                .map(ArticleDto::from)
                .orElseThrow(()-> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }


    //게시글 저장
    public void saveArticle(ArticleDto articleDto) {


        Set<Hashtag> hashtags = renewHashtagsFromContent(articleDto.content());

        Article article = articleDto.toEntity();
        article.addHashtags(hashtags);
        articleRepository.save(article);
    }
    //게시글 수정
    public void updateArticle(Long articleId, ArticleDto articleDto) {
        try{
            Article article = articleRepository.getReferenceById(articleId);
            UserAccount userAccount = userAccountRepository.getReferenceById(articleDto.userAccountDto().userId());


            if( article.getUserAccount().equals(userAccount) ){
                if( articleDto.title() != null){ article.setTitle(articleDto.title()); }
                if( articleDto.content() != null )article.setContent(articleDto.content());

                //1. 게시글에서 해시태그를 찾아내고
                Set<Long> hashtagIds = article.getHashtags().stream()
                        .map(Hashtag::getId)
                        .collect(Collectors.toUnmodifiableSet());

                //2. 해당 게시글의 해시태그를 모두 일단 clear 없애고 flush를 한다.
                article.clearHashtags();
                articleRepository.flush();

                //3. hashtagid로부터 deleteHashtagWithoutArticles 해당 해시태그의 게시글이 없으면 해시태그를 모두지워버린다.
                // 먼저 clearHashtags로 지운다음에 해당 메서드를 실행하니까 isEmpty()즉 해당 해시태그가 마지막 게시글의 해시테크였다면 지우는거다.
                hashtagIds.forEach(hashtagService::deleteHashtagWithoutArticles);


                //4. 그리고 다시 rewnewHashtagsFromContent로 파싱해서 다시 article.addHashtags로 추가한다.
                Set<Hashtag> hashtags = renewHashtagsFromContent(articleDto.content());
                article.addHashtags(hashtags);
            }
        } catch (EntityNotFoundException e) {
            log.warn("게시글 업데이트 실패. 게시글을 수정하는데 필요한 정보를 찾을 수 없습니다 - dto: {}", articleDto);
        }

        // Service레벨에 @Transactional 레벨에 묶여있어서 영속성 컨텍스트는 Article이 변한걸 감지해낸다.
        // 그 감지된거에 대해서 Query를 날린다. 그래서 자동으로 업데이트 Query가 실행이된다.
        // 단지 코드레벨에서 명시하고 싶으면 해도된다.
        // articleRepository.save(article);
    }
    //게시글 삭제
    public void deleteArticle(Long articleId, String userId) {
        Article article = articleRepository.getReferenceById(articleId);
        Set<Long> hashtagIds = article.getHashtags().stream()
                .map(Hashtag::getId)
                .collect(Collectors.toUnmodifiableSet());

        //1. 게시글삭제
        articleRepository.deleteByIdAndUserAccount_UserId(articleId, userId);
        articleRepository.flush();

        //2. 해당 게시글없으면 해시테크도 같이 삭제
        hashtagIds.forEach(hashtagService::deleteHashtagWithoutArticles);


    }

    public long getArticleCount() {
        return articleRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesViaHashtag(String hashtagName , Pageable pageable) {
        if(hashtagName == null || hashtagName.isBlank()){
            return Page.empty(pageable);
        }
        return articleRepository.findByHashtagNames(List.of(hashtagName), pageable)
                .map(ArticleDto::from);
    }

    public List<String> getHashtags() {
        return hashtagRepository.findAllHashtagNames(); // TODO: HashtagService 로 이동을 고려해보자.
    }


    private Set<Hashtag> renewHashtagsFromContent(String content) {
        //1. 본문안에서 파싱해야될 content들을 뽑아낸다.
        Set<String> hashtagNamesInContent = hashtagService.parseHashtagNames(content);

        //2. 그거를 다시 hashtagService에서 해시태그를 찾아낸다.
        Set<Hashtag> hashtags = hashtagRepository.findByHashtagNameIn(hashtagNamesInContent);

        //3. 찾아낸 해시태그 Entity에서 hashtagName을 찾아온다.
        Set<String> existingHashtagNames = hashtags.stream()
                .map(Hashtag::getHashtagName)
                .collect(Collectors.toUnmodifiableSet());
        //4. hashtagNamesInContent본문에서 해시태그 파싱한목록으로
        hashtagNamesInContent.forEach(newHashtagName -> {

            //existingHashtagNames (DB에 존재하는 해시태그이름)이 아니라면 조건문 탄다.
            if(!existingHashtagNames.contains(newHashtagName)){
                hashtags.add(Hashtag.of(newHashtagName));
            }
        });
        return hashtags;
    }
}
