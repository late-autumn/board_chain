package board.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import board.board.entity.UserEntity;
import board.board.repository.UserRepository;

@Service
public class UserService {

//	  @Override // 기본적인 반환 타입은 UserDetails, UserDetails를 상속받은 UserInfo로 반환 타입 지정 (자동으로 다운 캐스팅됨)
//	  public UserInfo loadUserByUsername(String id) throws UsernameNotFoundException { // 시큐리티에서 지정한 서비스이기 때문에 이 메소드를 필수로 구현
//	    return userRepository.findByEmail(email)
//	        .orElseThrow(() -> new UsernameNotFoundException((email)));
//	  }
	
	@Autowired
	UserRepository userRepository;
	
	public UserEntity findByUserId(String userId) {
		return userRepository.findByUserId(userId);
	}
	
	
}
