package com.cdac.dreamblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cdac.dreamblog.model.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long> {

}