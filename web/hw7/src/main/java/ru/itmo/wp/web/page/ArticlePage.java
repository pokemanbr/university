package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.ArticleService;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class ArticlePage {
    private final ArticleService articleService = new ArticleService();

    private void action(HttpServletRequest request, Map<String, Object> view) {
        if (request.getSession().getAttribute("user") == null) {
            request.getSession().setAttribute("message", "You should enter to post an article");
            throw new RedirectException("/index");
        }
    }

    private void posting(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        if (request.getSession().getAttribute("user") != null) {
            Article article = new Article();
            article.setTitle(request.getParameter("title"));
            article.setText(request.getParameter("text"));
            article.setHidden(false);
            User user = (User) request.getSession().getAttribute("user");
            article.setUserId(user.getId());
            articleService.validatePost(article);
            article.setTitle(article.getTitle().trim());
            article.setText(article.getText().trim());
            articleService.post(article);
            request.getSession().setAttribute("message", "The article is posted");
        } else {
            request.getSession().setAttribute("message", "You should enter to post an article");
        }
        throw new RedirectException("/index");
    }
}
