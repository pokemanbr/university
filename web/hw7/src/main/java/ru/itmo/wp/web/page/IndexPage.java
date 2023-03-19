package ru.itmo.wp.web.page;

import com.google.common.base.Strings;
import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.service.ArticleService;
import ru.itmo.wp.model.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @noinspection unused
 */
public class IndexPage {
    private final ArticleService articleService = new ArticleService();
    private final UserService userService = new UserService();

    private void action(HttpServletRequest request, Map<String, Object> view) {
        putMessage(request, view);
    }

    private void putMessage(HttpServletRequest request, Map<String, Object> view) {
        String message = (String) request.getSession().getAttribute("message");
        if (!Strings.isNullOrEmpty(message)) {
            view.put("message", message);
            request.getSession().removeAttribute("message");
        }
    }

    private void findArticles(HttpServletRequest request, Map<String, Object> view) {
        List<Article> articles = articleService.findAllNotHidden();
        view.put("articles", articles);
        Map<Long, String> userLoginOfArticle = new HashMap<>();
        for (Article article : articles) {
            userLoginOfArticle.put(article.getUserId(), userService.find(article.getUserId()).getLogin());
        }
        view.put("logins", userLoginOfArticle);
    }
}
