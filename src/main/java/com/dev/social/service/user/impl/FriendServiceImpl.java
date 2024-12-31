package com.dev.social.service.user.impl;

import com.dev.social.dto.response.FriendResponseDTO;
import com.dev.social.dto.result.FriendResult;
import com.dev.social.entity.Friend;
import com.dev.social.repository.FriendRepository;
import com.dev.social.repository.UserRepository;
import com.dev.social.service.user.FriendService;
import com.dev.social.service.user.UserService;
import com.dev.social.utils.enums.FriendEnum;
import com.dev.social.utils.exception.AppException;
import com.dev.social.utils.exception.ErrorMessage;
import com.dev.social.utils.mapping.MapUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class FriendServiceImpl implements FriendService {

    FriendRepository friendRepository;
    UserRepository userRepository;
    UserService userService;
    MapUtils mapUtils;


    @Override
    public void sendFriendRequest(String receiverId) {
        String senderId = userService.getCurrentUser().getId();
        if (senderId.equals(receiverId))
            throw new AppException(ErrorMessage.SAME_USER);

        friendRepository.findByUserIdAndFriendId(senderId, receiverId)
                .ifPresent(friend -> {
                    if (FriendEnum.REQUESTED.equals(friend.getStatus())) {
                        friend.setStatus(FriendEnum.ACCEPTED);
                        friendRepository.save(friend);
                    }
                });
        friendRepository.findByUserIdAndFriendId(receiverId, senderId)
                .ifPresentOrElse(friend -> {
                    if (FriendEnum.REQUESTED.equals(friend.getStatus()))
                        friendRepository.deleteById(friend.getId());
                }, () -> {
                    friendRepository.save(Friend.builder()
                            .user(userRepository.findById(receiverId)
                                    .orElseThrow(() -> new AppException(ErrorMessage.USER_NOT_FOUND)))
                            .friend(userService.getCurrentUser())
                            .status(FriendEnum.REQUESTED)
                            .build());
                });

    }

    @Override
    public void acceptFriendRequest(String friendId) {
        String userId = userService.getCurrentUser().getId();
        friendRepository.findByUserIdAndFriendId(userId, friendId)
                .ifPresentOrElse(friend -> {
                    if (FriendEnum.REQUESTED.equals(friend.getStatus())) {
                        friend.setStatus(FriendEnum.ACCEPTED);
                        friendRepository.save(friend);
                    }
                }, () -> {
                    throw new AppException(ErrorMessage.FORBIDDEN);
                });
    }

    @Override
    public void unfriend(String friendId) {
        String userId = userService.getCurrentUser().getId();
        friendRepository.findByUserIdAndFriendId(userId, friendId)
                .ifPresentOrElse(friend -> {
                    if (FriendEnum.ACCEPTED.equals(friend.getStatus())) {
                        friend.setStatus(FriendEnum.UNFRIEND);
                        friendRepository.save(friend);
                    }
                }, () -> {
                    throw new AppException(ErrorMessage.FORBIDDEN);
                });
    }

    @Override
    public void block(String friendId) {
        String userId = userService.getCurrentUser().getId();
        friendRepository.findByUserIdAndFriendId(userId, friendId)
                .ifPresentOrElse(friend -> {
                    if (FriendEnum.ACCEPTED.equals(friend.getStatus())) {
                        friend.setStatus(FriendEnum.BLOCKED);
                        friendRepository.save(friend);
                    }
                }, () -> {
                    throw new AppException(ErrorMessage.FORBIDDEN);
                });

    }

    @Override
    public List<FriendResponseDTO> getAllFriends() {
        String userId = userService.getCurrentUser().getId();
        return mapUtils.mapFriend(friendRepository.getAllFriends(userId));
    }

    @Override
    public List<FriendResponseDTO> getAllFriendsBlock() {
        String userId = userService.getCurrentUser().getId();
        return mapUtils.mapFriend(friendRepository.getAllFriendsBlock(userId));
    }

    @Override
    public List<FriendResponseDTO> getAllFriendsRequest() {
        String userId = userService.getCurrentUser().getId();
        return mapUtils.mapFriend(friendRepository.getAllFriendsRequest(userId));
    }
}
