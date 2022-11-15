package com.study.service.board;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.study.domain.board.BoardDto;
import com.study.domain.board.PageInfo;
import com.study.mapper.board.BoardMapper;
import com.study.mapper.board.ReplyMapper;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service // work, file add, file remove
@Transactional // all rollback
public class BoardSerivce {

	@Autowired
	private BoardMapper boardMapper;
	
	@Autowired
	private ReplyMapper replyMapper;
	
	@Autowired
	private S3Client s3Client;
	
	@Value("${aws.s3.bucket}")
	private String bucketName;
	
	public int register(BoardDto board, MultipartFile[] files) {
		// db에 게시물 정보 저장
		int cnt = boardMapper.insert(board);
		
		for (MultipartFile file : files) {
			
			if (file != null && file.getSize() > 0) {
				// db에 파일 정보 저장 (파일명, 게시물id)
				boardMapper.insertFile(board.getId(), file.getOriginalFilename());			
				
				uploadFile(board.getId(), file);
			}
		}
		
		return cnt;
	}

	private void uploadFile(int id, MultipartFile file) {
		try {
			// S3에 파일 저장
			// 키 생성
			String key = "prj1/board/" + id + "/" + file.getOriginalFilename();
			
			// putObjectRequest
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
					.bucket(bucketName)
					.key(key)
					.acl(ObjectCannedACL.PUBLIC_READ)
					.build();
			
			// requestBody
			RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
			
			// object(파일) 올리기
			s3Client.putObject(putObjectRequest, requestBody);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<BoardDto> listBoard(int page, String type, String keyword, PageInfo pageInfo) {
		int records = 10;
		int offset = (page - 1) * records;
	
		
		int countAll = boardMapper.countAll("%"+keyword+"%", type); // SELECT Count(*) FROM Board
		int lastPage = (countAll - 1) / records + 1;
		
		
		int leftPageNumber = (page - 1) / 10 * 10 + 1;
		int rightPageNumber = leftPageNumber + 9;
		int currentPageNumber = page;
		rightPageNumber = Math.min(rightPageNumber, lastPage);
		boolean hasNextPageNumber = page <= ((lastPage-1)/10*10);
		
		pageInfo.setHasNextPageNumber(hasNextPageNumber);
		pageInfo.setCurrentPageNumber(currentPageNumber);
		pageInfo.setLeftPageNumber(leftPageNumber);
		pageInfo.setRightPageNumber(rightPageNumber);
		pageInfo.setLastPageNumber(lastPage);
		
		/* int seachCountAll = mapper.seachCountAll(); */
		return boardMapper.list(offset, records, type, "%" + keyword + "%");
	}

	public BoardDto get(int id) {
		// TODO Auto-generated method stub
		return boardMapper.select(id);
	}

	
	public int update(BoardDto board, MultipartFile[] addFiles, List<String> removeFiles) {
		
		
		
		// removeFiles에 있는 파일명으로
		if (removeFiles != null)
		for (String fileName : removeFiles) {
			
			
			// 1. File 테이블에서 record 지우기
			boardMapper.deleteFileByBoardIdAndFileName(board.getId(), fileName);
									
			// 2. S3 저장소에 실제 파일 지우기
			deleteFile(board.getId(), fileName);

		};
		
		for (MultipartFile file : addFiles) {
			
			
			// File table에 해당 파일명 지우기
			boardMapper.deleteFileByBoardIdAndFileName(board.getId(), file.getOriginalFilename());
			
			
			if (file != null && file.getSize() > 0) {
				
				// File table에 파일명 추가
				boardMapper.insertFile(board.getId(), file.getOriginalFilename());			
				
				// S3 저장소에 실제 파일(Object) 추가
				uploadFile(board.getId(), file);
				
				
				/*
				 * File folder = new
				 * File("C:\\Users\\user\\Desktop\\study\\upload\\prj1\\board\\" +
				 * board.getId()); File dest = new File(folder, file.getOriginalFilename()); try
				 * { file.transferTo(dest); } catch (IOException e) { e.printStackTrace(); throw
				 * new RuntimeException(e); }
				 */
			}
		}
		
		
		return boardMapper.update(board);
		
	}
	/*
	 * public int delete(BoardDto board) { // TODO Auto-generated method stub return
	 * boardMapper.delete(board);
	 * 
	 * }
	 */
	public int remove(int id) {
		BoardDto board = boardMapper.select(id);
		List<String> fileNames = board.getFileName();
		
		if (fileNames != null) {
			for (String fileName : fileNames) {
				// s3 저장소의 파일 지우기
				deleteFile(id, fileName);
			}
		}
		// db 파일 records 지우기
		boardMapper.deleteFileByBoardId(id);
		
		// 게시물의 댓글들 지우기
		replyMapper.deleteByBoardId(id);
		
		// int a = 3/0; // runtime exception -> all rollback
		//게시물 지우기
		return boardMapper.delete(id);
	}
	

	private void deleteFile(int id, String fileName) {
		String key = "prj1/board/" + id + "/" + fileName;
		DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();
		s3Client.deleteObject(deleteObjectRequest);
	}


	
}
