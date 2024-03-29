package ru.itmo.wp.model.repository;

import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.domain.User;

import java.util.List;

public interface TalkRepository {
    List<Talk> findAll();

    Talk find(long id);

    List<Talk> findByUser(User user);

    void save(Talk talk);
}
