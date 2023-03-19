package ru.itmo.wp.form;

import ru.itmo.wp.domain.Post;

import javax.persistence.Lob;
import javax.validation.constraints.*;
import java.util.Arrays;
import java.util.List;

public class PostContent {
    @NotNull
    @NotBlank
    @Size(min = 1, max = 60)
    private String title;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 65000)
    @Lob
    private String text;

    @NotNull
    @Size(max = 1000)
    @Pattern(regexp = "[a-z\\s]*", message = "Expected lowercase Latin letters and whitespaces")
    private String tags;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<String> toTags() {
        return Arrays.asList(getTags().trim().split("\\s"));
    }

    public Post toPost() {
        Post post = new Post();
        post.setTitle(getTitle());
        post.setText(getText());
        return post;
    }
}
