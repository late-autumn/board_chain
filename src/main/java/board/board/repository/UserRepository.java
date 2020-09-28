
package board.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import board.board.entity.BoardEntity;
import board.board.entity.BoardFileEntity;
import board.board.entity.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, Integer>{

	UserEntity findByUserId(String userId);
	
 
}