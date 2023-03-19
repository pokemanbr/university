package ru.itmo.wp.servlet;

import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JsonServlet extends HttpServlet {
    private final String USER_ATTRIBUTE = "user";

    private static final String AUTH = "/message/auth";
    private static final String FIND_ALL = "/message/findAll";
    private static final String ADD = "/message/add";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        switch (uri) {
            case AUTH:
                auth(request, response);
                break;
            case FIND_ALL:
                findAll(request, response);
                break;
            case ADD:
                add(request);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
        }
        response.setContentType("application/json");
    }

    private boolean isNotBlank(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private void auth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String user = request.getParameter(USER_ATTRIBUTE);
        if (user != null && isNotBlank(user)) {
            request.getSession().setAttribute(USER_ATTRIBUTE, user);
        } else if (request.getSession().getAttribute(USER_ATTRIBUTE) != null) {
            user = request.getSession().getAttribute(USER_ATTRIBUTE).toString();
        } else {
            user = "";
        }
        String json = new Gson().toJson(user);
        response.getWriter().print(json);
        response.getWriter().flush();
    }
    private static class PairInfoMessage {
        String user;

        String text;

        PairInfoMessage(String u, String t) {
            user = u;
            text = t;
        }
    }

    private final ArrayList<PairInfoMessage> messages = new ArrayList<>();

    private void add(HttpServletRequest request) {
        String text = request.getParameter("text");
        String user = request.getSession().getAttribute(USER_ATTRIBUTE).toString();
        if (isNotBlank(text)) {
            messages.add(new PairInfoMessage(user, text));
        }
    }

    private void findAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getSession().getAttribute(USER_ATTRIBUTE) != null) {
            String json = new Gson().toJson(messages);
            response.getWriter().print(json);
            response.getWriter().flush();
        }
    }
}
