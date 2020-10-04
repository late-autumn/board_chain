
package board.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import board.board.entity.AccountEntity;

public interface AccountRepository extends CrudRepository<AccountEntity, Integer>{

//	List<AccountEntity> findAllByOrderByAccountIdxDesc();
//	Optional<AccountEntity> findByEmail(String email);
	
}