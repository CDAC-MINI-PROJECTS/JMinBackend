package com.cdac.dreamblog.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cdac.dreamblog.model.Dream;
import com.cdac.dreamblog.repository.DreamRepository;


@RestController
@RequestMapping("/api/dreams")
public class DreamController {
    @Autowired
    private DreamRepository dreamRepository;

    @PostMapping
    public Dream createDream(@RequestBody Dream dream) {
        return dreamRepository.save(dream);
    }

    @GetMapping
    public List<Dream> getAllDreams() {
        return dreamRepository.findAll();
    }
}
