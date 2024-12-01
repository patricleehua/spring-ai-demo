package com.patriclee.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {

    private String userId;
    private String userName;
    private String password;
    private String avatar;

}
