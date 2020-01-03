package ru.avkurbatov_home.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.avkurbatov_home.aspect.ControllerLogs;
import ru.avkurbatov_home.dao.abstracts.AccountDao;
import ru.avkurbatov_home.dao.abstracts.TopicDao;
import ru.avkurbatov_home.jdo.Topic;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;

/**
 * Controller for topic page
 * */
@Slf4j
@Controller
@ControllerLogs
@RequestMapping("/topics")
public class TopicsController {
    private static final String TOPICS_FORM = "topics";

    private final TopicDao topicDao;
    private final AccountDao accountDao;

    @Inject
    public TopicsController(TopicDao topicDao, AccountDao accountDao) {
        this.topicDao = topicDao;
        this.accountDao = accountDao;
    }

    @ModelAttribute("topic")
    public Topic topic(){
        return new Topic();
    }

    @GetMapping
    public String topicForm(HttpSession session, Principal principal) {
        addUserInfo(session, principal);
        return TOPICS_FORM;
    }

    @PostMapping("/post")
    public String post(@Valid Topic topic, Errors errors){
        if (errors.hasErrors()) {
            log.warn("topic has errors: {}", errors.getAllErrors());
            return TOPICS_FORM;
        }

        topicDao.save(topic);
        return TOPICS_FORM;
    }

    private void addUserInfo(HttpSession session, Principal principal) {
        if (principal == null) {
            return;
        }
        String username = principal.getName();
        session.setAttribute("user", accountDao.findByUsername(username));
    }

}
