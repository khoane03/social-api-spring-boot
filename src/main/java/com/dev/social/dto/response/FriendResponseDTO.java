package com.dev.social.dto.response;

import com.dev.social.entity.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendResponseDTO {

    String friendId;
    String name;

    public FriendResponseDTO(User user) {
        this.setFriendId(user.getId());
        this.setName(user.getName());
    }
}
