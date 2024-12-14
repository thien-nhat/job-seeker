package com.thien.jobseeker.domain.response;

import java.time.Instant;

import com.thien.jobseeker.util.constant.GenderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResUserDTO {
    private long id;
    private String name;
    private String email;
    private int age;
    private GenderEnum gender;
    private String address;
    private String refeshToken;
    private Instant createdAt;
    private Instant updatedAt;
}
