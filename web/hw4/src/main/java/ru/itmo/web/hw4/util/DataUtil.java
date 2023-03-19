package ru.itmo.web.hw4.util;

import ru.itmo.web.hw4.model.Post;
import ru.itmo.web.hw4.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ru.itmo.web.hw4.model.Colors.*;

public class DataUtil {
    private static final List<User> USERS = Arrays.asList(
            new User(1, "MikeMirzayanov", "Mike Mirzayanov", GREEN),
            new User(6, "pashka", "Pavel Mavrin", BLUE),
            new User(9, "geranazavr555", "Georgiy Nazarov", RED),
            new User(11, "tourist", "Gennady Korotkevich", GREEN)
    );

    private static final List<Post> POSTS = Arrays.asList(
            new Post(1, "Codeforces Round #510 (Div. 2)", "Hello, Codeforces\n" +
                    "Codeforces Round #510 (Div. 2) will start at Monday, September 17, 2018 at 11:05. The round will be " +
                    "rated for Div. 2 contestants (participants with the rating below 2100). Div. 1 participants can take " +
                    "a part out of competition as usual.\n" +
                    "This round is held on the tasks of the school stage All-Russian Olympiad of Informatics 2018/2019 " +
                    "year in city Saratov. The problems were prepared by PikMike, fcspartakm, Ne0n25, BledDest, Ajosteen " +
                    "and Vovuh. Great thanks to our coordinator _kun_ for the help with the round preparation! I also " +
                    "would like to thank our testers DavidDenisov, PrianishnikovaRina, Decibit and Vshining.\n" +
                    "UPD: The scoring distribution is 500-1000-1500-2000-2250-2750.", 1),
            new Post(2, "Few chars in the text", "There are really few symbols here\nTruth", 9),
            new Post(6, "one", "1", 6),
            new Post(11, "Трансляции на Codeforces", "Всем привет!\n" +
                    "\n" +
                    "Сегодня на Codeforces появилась поддержка трансляций (стримов)! Спасибо Геннадию tourist Короткевичу за отличную идею!\n" +
                    "\n" +
                    "Поддерживаются две популярные платформы — Twitch и YouTube. Пока возможность добавлять трансляции на Codeforces открыта красным участникам и по индивидуальным приглашениям. Позднее, мы, возможно, откроем эту возможность большему числу пользователей.\n" +
                    "\n" +
                    "Идея состоит в том, чтобы стримеры могли анонсировать свои трансляции на аудиторию Codeforces. Для этого трансляцию нужно добавить на сайт, используя специальный раздел в профиле. Обратите внимание, что при добавлении Twitch-трансляции вам надо указать просто URL вашего канала, а при добавлении YouTube-трансляции — её уникальную короткую ссылку (ссылку на видео youtu.be/…).\n" +
                    "\n" +
                    "Незадолго до старта трансляции в сайдбаре справа появится уведомление о предстоящей трансляции, которое будут видеть все посетители сайта.\n" +
                    "\n" +
                    "Если вы указали название трансляции только на русском языке, то подразумевается, что рабочий язык трансляции — русский, и она будет видна исключительно в русском интерфейсе.\n" +
                    "\n" +
                    "Например, прямо сейчас в русскоязычном интерфейсе есть анонс лекции Павла pashka Маврина в рамках его занятий в ИТМО по теме \"игры на графах\".\n" +
                    "\n" +
                    "Трансляции в сайдбаре\n" +
                    "\n" +
                    "Мы встроили просмотр трансляций прямо в интерфейс Codeforces. Чат YouTube-трансляции оказалось встроить непросто, пока такой возможности нет. Для трансляций Twitch чат будет виден на Codeforces.\n" +
                    "\n" +
                    "В наших планах еще немного улучшить эту функциональность. Как вы думаете, какие улучшения были бы полезны? Действительно ли термин \"Трансляция\" на русском подходящий? Или следует использовать \"Стрим\"?", 9)
    );

    public static void addData(HttpServletRequest request, Map<String, Object> data) {
        data.put("users", USERS);

        for (User user : USERS) {
            if (Long.toString(user.getId()).equals(request.getParameter("logged_user_id"))) {
                data.put("user", user);
            }
        }

        data.put("posts", POSTS);
    }
}
