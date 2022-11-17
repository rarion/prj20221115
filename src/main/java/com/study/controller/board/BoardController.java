package com.study.controller.board;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.study.domain.board.BoardDto;
import com.study.domain.board.PageInfo;
import com.study.service.board.BoardService;


@Controller
@RequestMapping("board")
public class BoardController {

	@Autowired
	private BoardService service;
	
	@GetMapping("home")
	public void home() {

	}

	@GetMapping("register")
	public void register() {
		// 게시물 작성 view로 포워드
		// /WEB-INF/views/board/register.jsp
	}

	@PostMapping("register")
	public String register(BoardDto board, MultipartFile[] files, RedirectAttributes rttr) {
		// * 파일 업로드
		// 1. web.xml : dispatcherServlet 설정에 multipart-config 추가
		// 2. form의 enctype = "multipart/form-data" 속성 추가
		// 3. controller의 메소드 argument type : MultipartFile
		
		/*
		 * System.out.println(files.length); 
		 * for(MultipartFile file : files) {
		 * 	System.out.println(file.getOriginalFilename()); 
		 * }
		 */
		
		int cnt = service.register(board, files);
		// int cnt = service.register(board, file);
	
		if (cnt == 1) {
			rttr.addFlashAttribute("message", "새 게시물이 등록되었습니다.");
		} else {
			rttr.addFlashAttribute("message", "새 게시물이 등록되지 않았습니다.");
		}
		
		return "redirect:/board/list";
	}

	@GetMapping("list")
	public void list(@RequestParam(name="page", defaultValue = "1") int page,
						@RequestParam(name="t", defaultValue = "all") String type,
						@RequestParam(name="q", defaultValue = "") String keyword,						
						PageInfo pageInfo,
						Model model) {
		
		
		
		List<BoardDto> list = service.listBoard(page,type, keyword, pageInfo);

		model.addAttribute("boardList", list);

	}

	@GetMapping("get")
	public void get(int id, Model model, Authentication authentication ) {
		
		String username = null;

		if (authentication != null) {
			username = authentication.getName();
		}
		
		// business logic ( db에서 게시물 가져오기 )
		BoardDto board = service.get(id, username);
		System.out.println(board);

		model.addAttribute("board",board);
	}
	
	@GetMapping("modify")
	@PreAuthorize("@boardSecurity.checkWriter(authentication.name, #id) ")
	public void modify(int id, Model model) {
		// business logic ( db에서 게시물 가져오기 )
		BoardDto board = service.get(id);
		System.out.println(board);

		model.addAttribute("board",board);
	}
	
	@PostMapping("modify")
	@PreAuthorize("@boardSecurity.checkWriter(authentication.name, #board.id) ")
	public String modify(BoardDto board,
						 // @RequestParam("files") 기본타입 or String일시 생략
						 MultipartFile[] files,
						 @RequestParam(name = "removeFiles", required = false) List<String> removeFiles,
						 RedirectAttributes rttr) {
		
		// 지울 파일명 들어오는 지 확인
		System.out.println("지울 파일명####");
		if (removeFiles != null) {
			for (String name : removeFiles) {
				System.out.println(name);
			}
		}
		
		int cnt = service.update(board, files, removeFiles);
		
		if(cnt==1) {	
			rttr.addFlashAttribute("message",  board.getId()+"번 게시물이 수정되었습니다");
		} else {
			rttr.addFlashAttribute("message", board.getId()+"번 게시물의 수정을 실패하였습니다");
		}
		
		return "redirect:/board/list";
	}
	
	@PostMapping("remove")
	@PreAuthorize("@boardSecurity.checkWriter(authentication.name, #id) ")
	public String remove(int id, RedirectAttributes rttr) {
		int cnt = service.remove(id);
		
		if (cnt == 1) {
			// id번 게시물이 삭제되었습니다.
			rttr.addFlashAttribute("message", id + "번 게시물이 삭제되었습니다.");
		} else {
			// id번 게시물이 삭제되지 않았습니다.
			rttr.addFlashAttribute("message", id + "번 게시물이 삭제되지 않았습니다.");
		}
		
		return "redirect:/board/list";
	}
	
	@PutMapping("like")
	@ResponseBody
	@PreAuthorize("isAuthenticated()")
	public Map<String, Object> like(@RequestBody Map<String, String> req, Authentication authentication ) {
		System.out.println(req);
		
		Map<String, Object> result =  service.updateLike(req.get("boardId"), authentication.getName());
		
		return result;
	}

}
