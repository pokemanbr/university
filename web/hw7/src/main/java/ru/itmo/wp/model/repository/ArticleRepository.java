package ru.itmo.wp.model.repository;

import ru.itmo.wp.model.domain.Article;

import java.util.List;

public interface ArticleRepository {
    Article find(long id);

    void save(Article article);

    List<Article> findAll();

    List<Article> findByUserId(long id);

    List<Article> findAllNotHidden();

    void changePrivacy(Article article);
}
