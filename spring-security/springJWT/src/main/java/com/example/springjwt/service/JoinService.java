package com.example.springjwt.service;

import com.example.springjwt.dto.JoinDto;
import com.example.springjwt.entity.UserEntity;
import com.example.springjwt.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(JoinDto joinDto) {

        String username = joinDto.getUsername();
        String password = joinDto.getPassword();

        // 존재하는지 확인
        Boolean isExist = userRepository.existsByUsername(username);
        if (isExist) {

            return;
        }

        // 없으면 생성
        UserEntity data =new UserEntity();

        data.setUsername(username);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        // spring은 앞 단에 접두사(ex.ROLE) 가지고 그 뒤에 우리가 원하는 권한을 적으면 된다.
        data.setRole("ROLE_ADMIN");

        userRepository.save(data);
    }
}
