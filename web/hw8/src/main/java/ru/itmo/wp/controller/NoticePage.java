package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.itmo.wp.form.NoticeContent;
import ru.itmo.wp.form.validator.NoticeContentValidator;
import ru.itmo.wp.service.NoticeService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class NoticePage extends Page {
    private final NoticeService noticeService;
    private final NoticeContentValidator noticeContentValidator;

    public NoticePage(NoticeService noticeService, NoticeContentValidator noticeContentValidator) {
        this.noticeService = noticeService;
        this.noticeContentValidator = noticeContentValidator;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(noticeContentValidator);
    }

    @GetMapping("/notice")
    public String noticeGet(Model model, HttpSession httpSession) {
        if (getUser(httpSession) == null) {
            setMessage(httpSession, "You need to enter to make notices");
            return "redirect:";
        }
        model.addAttribute("noticeForm", new NoticeContent());
        return "NoticePage";
    }

    @PostMapping("/notice")
    public String noticePost(@Valid @ModelAttribute("noticeForm") NoticeContent noticeForm,
                         BindingResult bindingResult,
                         HttpSession httpSession) {
        if (bindingResult.hasErrors()) {
            return "NoticePage";
        }

        noticeService.makeNotice(noticeForm);
        setMessage(httpSession, "The notice has been successfully published");

        return "redirect:";
    }
}
