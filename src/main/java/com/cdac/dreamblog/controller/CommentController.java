package com.cdac.dreamblog.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cdac.dreamblog.model.Comment;
import com.cdac.dreamblog.repository.CommentRepository;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;

    @PostMapping
    public Comment createUser(@RequestBody Comment user) {
        return commentRepository.save(user);
    }

    @GetMapping
    public List<Comment> getAllCommentByPost() {
        return commentRepository.findAll();
    }
}
