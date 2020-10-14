
package board.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import board.board.entity.BoardEntity;
import board.board.entity.BoardFileEntity;

@Mapper
public interface BoardMapper {
	List<BoardEntity> selectBoardList() throws Exception;
	
	void insertBoard(BoardEntity board) throws Exception;

	BoardEntity selectBoardDetail(int boardIdx) throws Exception;

	void updateHitCount(int boardIdx) throws Exception;
	
	void updateBoard(BoardEntity board) throws Exception;
	
	void deleteBoard(int boardIdx) throws Exception;

	void insertBoardFileList(List<BoardFileEntity> list) throws Exception;

	List<BoardFileEntity> selectBoardFileList(int boardIdx) throws Exception;

	BoardFileEntity selectBoardFileInformation(@Param("idx") int idx, @Param("boardIdx" )int boardIdx);
}