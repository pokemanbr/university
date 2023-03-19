package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.ArticleService;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class MyArticlesPage {
    private final ArticleService articleService = new ArticleService();

    private void action(HttpServletRequest request, Map<String, Object> view) {
        if (request.getSession().getAttribute("user") != null) {
            User user = (User) request.getSession().getAttribute("user");
            view.put("articles", articleService.findByUser(user.getId()));
        } else {
            request.getSession().setAttribute("message", "You should enter to view your articles");
            throw new RedirectException("/index");
        }
    }

    private void changePrivacy(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        User user = (User) request.getSession().getAttribute("user");

        Article article = articleService.validateChangingPrivacy(user, request.getParameter("postId"),
                request.getParameter("hidden"));

        articleService.changePrivacy(article);
    }
}
