package board.board.service;

import java.util.List;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import board.board.entity.AccountEntity;
import board.board.entity.BoardEntity;
import board.board.entity.BoardFileEntity;
 
public interface AccountService {

//	List<BoardEntity> selectBoardList() throws Exception;
//
	void saveAccount(AccountEntity account);
//	
//	BoardEntity selectBoardDetail(int boardIdx) throws Exception;
//
//	void deleteBoard(int boardIdx);
//
//	BoardFileEntity selectBoardFileInformation(int boardIdx, int idx) throws Exception;
//	AccountEntity loadUserByUsername(String email)throws Exception;
}