package com.dev.social.service.user.impl;

import com.dev.social.dto.response.UserResponseDTO;
import com.dev.social.entity.User;
import com.dev.social.repository.UserRepository;
import com.dev.social.service.user.UserService;
import com.dev.social.utils.exception.AppException;
import com.dev.social.utils.exception.ErrorMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

    UserRepository userRepository;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDTO> getAllUser(int pageIndex, int pageSize) {
        return userRepository.getAllUsers(pageIndex - 1,   pageSize)
                .stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void setStatus(String id) {
        userRepository.setStatus(id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void setVerification(String id) {
        userRepository.setVerification(id);
    }


    @Override
    public UserResponseDTO getInfo() {
        return new UserResponseDTO(getCurrentUser());
    }

    @Override
    public User getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
        {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorMessage.USER_NOT_FOUND));
        }
        throw new AppException(ErrorMessage.UNAUTHORIZED);

    }
}
