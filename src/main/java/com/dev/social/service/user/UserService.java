package com.dev.social.service.user;

import com.dev.social.dto.response.UserResponseDTO;
import com.dev.social.entity.User;

import java.util.List;

public interface UserService {
    List<UserResponseDTO> getAllUser(int pageIndex, int pageSize);

    UserResponseDTO getInfo(String id);

    void setStatus(String id);

    void setVerification(String id);

    User getCurrentUser();

}
