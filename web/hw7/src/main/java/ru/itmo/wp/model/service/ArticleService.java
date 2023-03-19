package ru.itmo.wp.model.service;

import com.google.common.base.Strings;
import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.repository.ArticleRepository;
import ru.itmo.wp.model.repository.impl.ArticleRepositoryImpl;

import java.util.List;

public class ArticleService {
    private final ArticleRepository articleRepository = new ArticleRepositoryImpl();

    public void post(Article article) {
        articleRepository.save(article);
    }

    public void validatePost(Article article) throws ValidationException {
        if (Strings.isNullOrEmpty(article.getTitle().trim())) {
            throw new ValidationException("Title is required");
        }
        if (article.getTitle().trim().length() > 50) {
            throw new ValidationException("Text is longer than 50 characters");
        }
        if (Strings.isNullOrEmpty(article.getText().trim())) {
            throw new ValidationException("Text is required");
        }
        if (article.getText().trim().length() > 10000) {
            throw new ValidationException("Text is too big");
        }
    }

    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    public List<Article> findByUser(long id) {
        return articleRepository.findByUserId(id);
    }

    public List<Article> findAllNotHidden() {
        return articleRepository.findAllNotHidden();
    }

    public Article findById(long postId) {
        return articleRepository.find(postId);
    }

    public void changePrivacy(Article article) {
        articleRepository.changePrivacy(article);
    }

    private String nameForHidden(boolean hidden) {
        return hidden ? "Show" : "Hide";
    }

    public Article validateChangingPrivacy(User user, String id, String buttonLabel) throws ValidationException {
        if (user == null) {
            throw new ValidationException("You should enter to change privacy of your articles");
        }

        long postId;

        try {
            postId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new ValidationException("Id should contain only numbers");
        }

        Article article = findById(postId);
        if (article.getUserId() != user.getId()) {
            throw new ValidationException("You can change only your articles");
        }

        if (!nameForHidden(article.isHidden()).equals(buttonLabel)) {
            throw new ValidationException("Reverse");
        }

        return article;
    }
}
