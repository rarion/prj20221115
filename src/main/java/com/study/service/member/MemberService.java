package com.study.service.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.study.domain.member.MemberDTO;
import com.study.mapper.member.MemberMapper;

@Service
public class MemberService {

	@Autowired
	private MemberMapper mapper;
	
	public int insert(MemberDTO member) {
		
		return mapper.insert(member);
	}

	public List<MemberDTO> list() {
		
		return mapper.selectAll();
	}

	public MemberDTO get(String id) {
		
		return mapper.select(id);
	}
	
	public MemberDTO getEmail(String email) {
		
		return mapper.selectEmail(email);
	}

	public MemberDTO getNickName(String nickName) {
		
		return mapper.selectNickName(nickName);
	}

	public int update(MemberDTO member) {
		
		int cnt = 0;
		
		try {
			return mapper.update(member);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cnt; 
	}

	public int remove(String id) {
		
		return mapper.delete(id);
	}



	
}
