package chemlab.config;

import chemlab.domain.model.user.User;
import chemlab.infrastructure.storage.ImageStorageService;
import chemlab.shared.requests.CreateUserRequest;
import chemlab.shared.requests.RegisterUserRequest;
import chemlab.shared.requests.UpdateUserRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class CustomMapper {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    ImageStorageService imageStorageService;

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
    void updateUserFromDto(UpdateUserRequest dto, @MappingTarget User entity) {};

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
    void createUserFromDto(CreateUserRequest dto, @MappingTarget User entity) {};

    void registerUserFromDto(RegisterUserRequest dto, @MappingTarget User entity) {};
}
