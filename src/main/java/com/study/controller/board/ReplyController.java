package com.study.controller.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.study.domain.board.ReplyDTO;
import com.study.service.board.ReplyService;

@Controller
@RequestMapping("reply")
public class ReplyController {
	
	@Autowired
	private ReplyService service;
	
	@PutMapping("modify")
	@ResponseBody
	public Map<String, Object> modify(@RequestBody ReplyDTO reply) {
		Map<String, Object> map = new HashMap<>();
		
		int cnt = service.modify(reply);
		
		if (cnt == 1) {
			map.put("message", "댓글이 수정되었습니다.");
		} else {
			map.put("message", "댓글이 수정되지 않았습니다.");
		}
		
		return map;
	}
	
	@GetMapping("get/{id}")
	@ResponseBody
	public ReplyDTO get(@PathVariable int id) {
		return service.getById(id);
	}
	
	@DeleteMapping("remove/{id}")
	@ResponseBody
	public Map<String, Object> remove(@PathVariable int id) {
		Map<String, Object> map = new HashMap<>();

		int cnt = service.removeById(id);
		if (cnt == 1) {
			map.put("message", "댓글이 삭제되었습니다.");
		} else {
			map.put("message", "댓글이 삭제되지 않았습니다.");
		}
		return map;
	}
	
	@GetMapping("list/{boardId}")
	@ResponseBody
	public List<ReplyDTO> list(@PathVariable int boardId) {
		return service.listReplyByBoardId(boardId);
	}
	
	
	@PostMapping("add")
	@ResponseBody
	public Map<String, Object> add(@RequestBody ReplyDTO reply) {
//		System.out.println(reply);
		Map<String, Object> map = new HashMap<>();
		
		int cnt = service.addReply(reply);
		if (cnt ==1) {
			map.put("message", "새 댓글이 등록되었습니다.");
		} else {
			map.put("massage", "새 댓글 등록을 실패하였습니다.");
		}
		
		return map;
	}
}
