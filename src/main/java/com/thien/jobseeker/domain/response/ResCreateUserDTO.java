package com.thien.jobseeker.domain.response;

import java.time.Instant;
import com.thien.jobseeker.util.constant.GenderEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String email;
    private String name;
    private int age;
    private Instant createdAt;
    private GenderEnum gender;
    private String address;
}
