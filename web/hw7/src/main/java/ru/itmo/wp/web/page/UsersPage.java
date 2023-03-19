package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/** @noinspection unused*/
public class UsersPage {
    private final UserService userService = new UserService();

    private void action(HttpServletRequest request, Map<String, Object> view) {
        // No operations.
    }

    private void findAll(HttpServletRequest request, Map<String, Object> view) {
        view.put("users", userService.findAll());
        updateLoggedUser(request, view);
    }

    private void findUser(HttpServletRequest request, Map<String, Object> view) {
        view.put("user", userService.find(Long.parseLong(request.getParameter("userId"))));
    }

    private void updateLoggedUser(HttpServletRequest request, Map<String, Object> view) {
        if (request.getSession().getAttribute("user") != null) {
            User user = userService.find(((User) request.getSession().getAttribute("user")).getId());
            request.getSession().setAttribute("user", userService.find(user.getId()));
            view.put("loggedUser", request.getSession().getAttribute("user"));
        } else {
            view.remove("loggedUser");
        }
    }

    private void changeRights(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        updateLoggedUser(request, view);
        User user = (User) request.getSession().getAttribute("user");
        User changedUser = userService.validateChangeRights(user, request.getParameter("admin"),
                request.getParameter("id"));

        userService.changeRights(changedUser);
        updateLoggedUser(request, view);
    }
}
