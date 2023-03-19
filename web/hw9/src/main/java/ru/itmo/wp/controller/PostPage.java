package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.itmo.wp.domain.Comment;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.security.Guest;
import ru.itmo.wp.service.PostService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class PostPage extends Page {
    private final PostService postService;

    public PostPage(PostService postService) {
        this.postService = postService;
    }

    @Guest
    @GetMapping({"/post/{id}", "/post"})
    public String postGet(@PathVariable(required = false) String id, Model model, HttpSession httpSession) {
        try {
            model.addAttribute("post", postService.findById(Long.parseLong(id)));
            if (getUser(httpSession) != null) {
                model.addAttribute("commentForm", new Comment());
            }
        } catch (NumberFormatException ignored) {
            //No operations.
        }
        return "PostPage";
    }

    @Guest
    @PostMapping({"/post/{id}", "/post"})
    public String postPost(@PathVariable(required = false) String id,
                           @Valid @ModelAttribute("commentForm") Comment comment,
                           BindingResult bindingResult, Model model, HttpSession httpSession) {
        if (bindingResult.hasErrors()) {
            putMessage(httpSession, "The comment is empty");
            return "redirect:/post/" + id;
        }
        Post post;
        try {
            post = postService.findById(Long.parseLong(id));
        } catch (NumberFormatException ignored) {
            putMessage(httpSession, "No such post.");
            return "redirect:";
        }
        if (getUser(httpSession) == null) {
            putMessage(httpSession, "You should be logged to write comments");
            return "redirect:/post/" + id;
        }
        postService.makePost(post, getUser(httpSession), comment);
        putMessage(httpSession, "The comment has been successfully published");
        return "redirect:/post/" + id;
    }
}
