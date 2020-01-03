package ru.avkurbatov_home.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.avkurbatov_home.aspect.ControllerLogs;
import ru.avkurbatov_home.dao.abstracts.MessageDao;
import ru.avkurbatov_home.jdo.Message;

import javax.inject.Inject;
import javax.validation.Valid;

/**
 * Controller for message page
 * */
@Slf4j
@Controller
@ControllerLogs
@RequestMapping("/messages")
public class MessagesController {
    private static final String MESSAGES_FORM = "messages";

    private final MessageDao messageDao;

    @Inject
    public MessagesController(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @ModelAttribute("message")
    public Message message(){
        return new Message();
    }

    @GetMapping
    @RequestMapping(value="/{topicId}")
    public String messagesForm(@PathVariable("topicId") int topicId, Model model) {
        model.addAttribute("topicId", topicId);
        return MESSAGES_FORM;
    }

    @PostMapping("/post")
    public String post(@Valid Message message, Errors errors, @ModelAttribute("topicId") int topicId){
        if (errors.hasErrors()) {
            return MESSAGES_FORM;
        }
        messageDao.save(message);
        return MESSAGES_FORM;
    }

}
