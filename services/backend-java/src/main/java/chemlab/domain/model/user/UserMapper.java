package chemlab.domain.model.user;

import chemlab.domain.service.user.UserAuthenticationService;
import chemlab.domain.service.user.UserProfileService;
import chemlab.shared.requests.CreateUserRequest;
import chemlab.shared.requests.RegisterUserRequest;
import chemlab.shared.requests.UpdateUserRequest;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.util.Date;

import static chemlab.security.user.Role.ROLE_USER;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    protected UserAuthenticationService userAuthenticationService;
    @Autowired
    protected UserProfileService userProfileService;

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "profileImgUrl", ignore = true)
    @Mapping(target = "lastLoginDate", ignore = true)
    @Mapping(target = "joinDate", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "highScore", ignore = true)
    @Mapping(target = "userFlashcards", ignore = true)
    public abstract void updateUserFromDto(UpdateUserRequest dto, @MappingTarget User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "profileImgUrl", ignore = true)
    @Mapping(target = "lastLoginDate", ignore = true)
    @Mapping(target = "joinDate", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "highScore", ignore = true)
    @Mapping(target = "userFlashcards", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "notLocked", ignore = true)
    public abstract User createUserFromDto(CreateUserRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "profileImgUrl", ignore = true)
    @Mapping(target = "lastLoginDate", ignore = true)
    @Mapping(target = "joinDate", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "highScore", ignore = true)
    @Mapping(target = "userFlashcards", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "notLocked", ignore = true)
    public abstract User registerUserFromDto(RegisterUserRequest dto);

    @AfterMapping
    protected void afterRegisterUser(@Nonnull RegisterUserRequest dto, @MappingTarget User entity) {
        entity.setJoinDate(new Date());
        entity.setActive(true);
        entity.setNotLocked(true);
        entity.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));

        String roleName = ROLE_USER.name();
        entity.setRole(roleName);
        entity.setAuthorities(ROLE_USER.getAuthorities());
    }

    @AfterMapping
    protected void afterCreateUser(@Nonnull CreateUserRequest dto, @MappingTarget User entity) {
        entity.setJoinDate(new Date());
        entity.setActive(dto.isActive());
        entity.setNotLocked(dto.isNotLocked());

        String roleName = StringUtils.isNotBlank(dto.getRole()) ? dto.getRole() : ROLE_USER.name();
        entity.setRole(roleName);
        entity.setAuthorities(userAuthenticationService.getRoleEnumName(roleName).getAuthorities());

        try {
            if (dto.getProfileImg() != null && !dto.getProfileImg().isEmpty()) {
                userProfileService.saveProfileImg(entity, dto.getProfileImg());
            } else {
                entity.setProfileImgUrl(userProfileService.getTemporaryProfileImageUrl(entity.getUsername()));
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to process profile image during user creation", e);
        }
    }
}
