package edu.metrostate.ics499.team2.model;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component @Slf4j
public class Mapper {
    public UserDAO toDao(User user) {
    	log.info("converting user to DAO");
        try {
//	        List<String> roles = user
//	          .getRoles()
//	          .stream()
//	          .map(Role::getName)
//	          .collect(Collectors.toList());
	        return new UserDAO(user.getUsername(), user.getRole());
        } catch(Exception e) {
        	log.warn("error converting user");
        	return null;
        }        
    }

    public User toUser(UserLoginDTO userDto) {
        return new User(userDto);
    }
}
