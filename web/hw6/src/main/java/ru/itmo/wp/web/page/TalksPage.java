package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.TalkService;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings({"unused"})
public class TalksPage extends Page {
    private final TalkService talkService = new TalkService();

    @Override
    public void before(HttpServletRequest request, Map<String, Object> view) {
        super.before(request, view);
        if (getUser() != null) {
            view.put("talks", talkService.findByUser(getUser()));
            view.put("users", userService.findAll());
        } else {
            setMessage("You should enter to use talks");
            throw new RedirectException("/index");
        }
    }

    public void send(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        User sourceUser = getUser();
        if (!userService.checkExistance(request.getParameter("targetUser"))) {
            throw new ValidationException("User should exist");
        }
        User targetUser = userService.findByLogin(request.getParameter("targetUser"));
        String text = request.getParameter("text");
        talkService.validateSend(text);
        Talk talk = new Talk();
        talk.setSourceUserId(sourceUser.getId());
        talk.setTargetUserId(targetUser.getId());
        talk.setText(text.trim());
        talkService.send(talk);
        throw new RedirectException("/talks");
    }
}
