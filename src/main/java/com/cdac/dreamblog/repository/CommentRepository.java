package com.cdac.dreamblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdac.dreamblog.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}


