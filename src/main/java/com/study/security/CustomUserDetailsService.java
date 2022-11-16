package com.study.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.study.domain.member.MemberDTO;
import com.study.mapper.member.MemberMapper;

@Component
public class CustomUserDetailsService implements UserDetailsService{
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MemberMapper mapper;
	
	@Override //interface
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		MemberDTO member = mapper.select(username);
		
		if(member == null) {
			return null;
		}
		
		/* String encodedPw = passwordEncoder.encode(member.getPassword()); ( db 저장시)*/ 
		
		User user = new User(username, member.getPassword(), List.of());
		
		
		return user;
	}

	
}
