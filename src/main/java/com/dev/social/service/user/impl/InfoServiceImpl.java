package com.dev.social.service.user.impl;

import com.dev.social.dto.request.user.UpdateUserInfo;
import com.dev.social.entity.Info;
import com.dev.social.entity.User;
import com.dev.social.repository.UserRepository;
import com.dev.social.service.user.InfoService;
import com.dev.social.utils.exception.AppException;
import com.dev.social.utils.exception.ErrorMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {

    UserRepository userRepository;

    @Override
    public void updateInfo(UpdateUserInfo req, String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorMessage.USER_NOT_FOUND));
        if (user.getInfo() == null) {
            user.setInfo(Info.builder()
                    .dob(req.getDob())
                    .Address(req.getAddress())
                    .gender(req.getGender())
                    .user(user)
                    .build());
        } else {
            Info info = user.getInfo();
            info.setDob(req.getDob());
            info.setAddress(req.getAddress());
            info.setGender(req.getGender());
            info.setUser(user);

        }
        userRepository.save(user);
    }
}