package com.shyun.boardproject.service;

import com.shyun.boardproject.domain.Article;
import com.shyun.boardproject.domain.type.SearchType;
import com.shyun.boardproject.dto.ArticleDto;
import com.shyun.boardproject.dto.ArticleWithCommentsDto;
import com.shyun.boardproject.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;


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
            case HASHTAG -> null;
        };
    }

    //게시글 상세조회
    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticle(Long articleId){
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from)
                .orElseThrow(()-> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));

    }
    //게시글 저장
    public void saveArticle(ArticleDto articleDto) {
        articleRepository.save(articleDto.toEntity());
    }
    //게시글 수정
    public void updateArticle(ArticleDto articleDto) {
        try{
            Article article = articleRepository.getReferenceById(articleDto.id());
            if( articleDto.title() != null){ article.setTitle(articleDto.title()); }
            if( articleDto.content() != null )article.setContent(articleDto.content());

        } catch (EntityNotFoundException e) {
            log.warn("게시글 업데이트 실패. 게시글을 찾을 수 없습니다 - dto: {}", articleDto);
        }

        // Service레벨에 @Transactional 레벨에 묶여있어서 영속성 컨텍스트는 Article이 변한걸 감지해낸다.
        // 그 감지된거에 대해서 Query를 날린다. 그래서 자동으로 업데이트 Query가 실행이된다.
        // 단지 코드레벨에서 명시하고 싶으면 해도된다.
        // articleRepository.save(article);
    }
    //게시글 삭제
    public void deleteArticle(Long articleId) {
        articleRepository.deleteById(articleId);
    }
}
