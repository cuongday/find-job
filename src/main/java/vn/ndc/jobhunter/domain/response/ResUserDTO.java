package vn.ndc.jobhunter.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.ndc.jobhunter.util.constant.GenderEnum;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ResUserDTO {
    Long id;
    String email;
    String name;
    GenderEnum gender;
    String address;
    int age;
    Instant updatedAt;
    Instant createdAt;
    CompanyUser company;
    RoleUser role;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyUser{
        Long id;
        String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleUser{
        Long id;
        String name;
    }
}
