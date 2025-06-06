package com.cdac.dreamblog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdac.dreamblog.model.Dream;
import com.cdac.dreamblog.model.User;

public interface DreamRepository extends JpaRepository<Dream, Long> {
     List<Dream> findByUser(User user);
}