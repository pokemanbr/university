package ru.itmo.wp.model.service;

import com.google.common.base.Strings;
import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.repository.TalkRepository;
import ru.itmo.wp.model.repository.impl.TalkRepositoryImpl;

import java.util.List;

public class TalkService {
    private final TalkRepository talkRepository = new TalkRepositoryImpl();

    public List<Talk> findAll() {
        return talkRepository.findAll();
    }


    public List<Talk> findByUser(User user) {
        return talkRepository.findByUser(user);
    }

    public void send(Talk talk) {
        talkRepository.save(talk);
    }

    public void validateSend(String text) throws ValidationException {
        if (Strings.isNullOrEmpty(text.trim())) {
            throw new ValidationException("Text is required");
        }
        if (text.trim().length() > 255) {
            throw new ValidationException("Text should be no longer than 255 characters");
        }
    }
}
