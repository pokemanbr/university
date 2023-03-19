package ru.itmo.wp.service;

import org.springframework.stereotype.Service;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.domain.Role;
import ru.itmo.wp.domain.Tag;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.form.UserCredentials;
import ru.itmo.wp.repository.PostRepository;
import ru.itmo.wp.repository.RoleRepository;
import ru.itmo.wp.repository.UserRepository;
import ru.itmo.wp.repository.TagRepository;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    /**
     * @noinspection FieldCanBeLocal, unused
     */
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PostRepository postRepository, TagRepository tagRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;

        this.roleRepository = roleRepository;
        for (Role.Name name : Role.Name.values()) {
            if (!roleRepository.existsByName(name)) {
                roleRepository.save(new Role(name));
            }
        }
    }

    public User register(UserCredentials userCredentials) {
        User user = new User();
        user.setLogin(userCredentials.getLogin());
        userRepository.save(user);
        userRepository.updatePasswordSha(user.getId(), userCredentials.getLogin(), userCredentials.getPassword());
        return user;
    }

    public boolean isLoginVacant(String login) {
        return userRepository.countByLogin(login) == 0;
    }

    public User findByLoginAndPassword(String login, String password) {
        return login == null || password == null ? null : userRepository.findByLoginAndPassword(login, password);
    }

    public User findById(Long id) {
        return id == null ? null : userRepository.findById(id).orElse(null);
    }

    public List<User> findAll() {
        return userRepository.findAllByOrderByIdDesc();
    }

    public void writePost(User user, Post post, List<String> tags) {
        for (String nameTag : tags) {
            if (!nameTag.isEmpty()) {
                Tag tag = tagRepository.findByName(nameTag);
                if (tag == null) {
                    tag = new Tag();
                    tag.setName(nameTag);
                    tagRepository.save(tag);
                }
                post.addTag(tag);
            }
        }
        user.addPost(post);
        post.setUser(user);
        postRepository.save(post);
    }
}
