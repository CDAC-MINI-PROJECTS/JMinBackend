package com.cdac.dreamblog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdac.dreamblog.model.Dream;

public interface DreamRepository extends JpaRepository<Dream, Long> {
}