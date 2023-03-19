package ru.itmo.wp.model.service;

import ru.itmo.wp.model.domain.Event;
import ru.itmo.wp.model.domain.Event.TypeStatus;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.repository.EventRepository;
import ru.itmo.wp.model.repository.impl.EventRepositoryImpl;

@SuppressWarnings("SqlNoDataSourceInspection")
public class EventService {
    private final EventRepository eventRepository = new EventRepositoryImpl();

    private void put(User user, TypeStatus type) {
        Event event = new Event();
        event.setType(type);
        event.setUserId(user.getId());

        eventRepository.save(event);
    }

    public void putEnter(User user) {
        put(user, TypeStatus.ENTER);
    }

    public void putLogout(User user) {
        put(user, TypeStatus.LOGOUT);
    }
}
