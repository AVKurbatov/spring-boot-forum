package ru.avkurbatov_home.controller;

import org.springframework.web.bind.annotation.*;
import ru.avkurbatov_home.aspect.ControllerLogs;
import ru.avkurbatov_home.dao.abstracts.TopicDao;
import ru.avkurbatov_home.jdo.Topic;

import javax.inject.Inject;
import java.util.List;

/**
 * Handling rest requests from topics page
 * */
@RestController
@ControllerLogs
@RequestMapping(path="/topics", produces="application/json")
@CrossOrigin(origins="*")
public class TopicsRestController {

    private final TopicDao topicDao;

    @Inject
    public TopicsRestController(TopicDao topicDao) {
        this.topicDao = topicDao;
    }

    @GetMapping("/findAll")
    public List<Topic> findAll(){
        return topicDao.findAll();
    }

    @GetMapping("/smth/save")
    public String smth(){
        return "ok";
    }
}
