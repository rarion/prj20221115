package com.study.domain.member;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MemberDTO {
	private String id;
	private String password;
	private String email;
	private String nickName;
	
	private LocalDateTime inserted;
	
}
