package com.shyun.boardproject.service;

import com.shyun.boardproject.domain.type.SearchType;
import com.shyun.boardproject.dto.ArticleDto;
import com.shyun.boardproject.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword) {
        return Page.empty();
    }

    @Transactional(readOnly = true)
    public List<ArticleDto> searchArticle(long l) {
        return null;
    }

    public void saveArticle(ArticleDto articleDto) {

    }
}
