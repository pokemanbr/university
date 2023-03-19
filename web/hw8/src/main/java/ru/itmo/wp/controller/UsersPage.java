package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.itmo.wp.form.NoticeContent;
import ru.itmo.wp.form.StatusInfo;
import ru.itmo.wp.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class UsersPage extends Page {
    private final UserService userService;

    public UsersPage(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/all")
    public String usersGet(Model model, HttpSession httpSession) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("statusInfo", new StatusInfo());
        model.addAttribute("loggedUser", getUser(httpSession));
        return "UsersPage";
    }

    @PostMapping("/users/all")
    public String usersPost(@Valid @ModelAttribute("statusInfo") StatusInfo statusInfo, HttpSession httpSession) {
        userService.changeStatus(statusInfo.getId(), !statusInfo.isDisabled());
        setMessage(httpSession, "The status of user has been successfully changed");
        return "redirect:/users/all";
    }
}
