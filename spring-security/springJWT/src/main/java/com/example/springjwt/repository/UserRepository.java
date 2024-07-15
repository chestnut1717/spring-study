package com.example.springjwt.repository;

import com.example.springjwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// 2개의 인자를 받음(ENTITY, ID의 TYPE)
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    // JPA구문(existBy 구문을 통해서 username이 존재하는지?)
    Boolean existsByUsername(String username);
}
