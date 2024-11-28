package com.lec.spring.config;

import com.lec.spring.domain.User;
import com.lec.spring.repository.AuthorityRepository;
import com.lec.spring.repository.UserRepository;
import org.apache.ibatis.session.SqlSession;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// UserDetailsService
// 컨테이너에 등록한다.
// 시큐리티 설정에서 loginProcessingUrl(url) 을 설정해 놓았기에
// 로그인시 위 url 로 요청이 오면 자동으로 UserDetailsService 타입으로 IoC 되어 있는
// loadUserByUsername() 가 실행되고
// 인증성공하면 결과를 UserDetails 로 리턴

@Service
public class PrincipalDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    public PrincipalDetailService(SqlSession sqlSession) {
        this.userRepository = sqlSession.getMapper(UserRepository.class);
        this.authorityRepository = sqlSession.getMapper(AuthorityRepository.class);
    }


    //스프링 시큐리티 로그인 검증의 id는 username이 디폴트. 이걸 사용하는게 좋음
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername(" + username + ") 호출");

        //DB 조회
        User user = userRepository.findByUsername(username);

        //해당 유저가 DB에 있다면 UserDetails 생성해서 리턴
        //DB 에 담긴 권한을 세션에 있는 UserDetails에 적용.
        if (user != null) {
            PrincipalDetails userDetails = new PrincipalDetails(user);
            userDetails.setAuthorityRepository(authorityRepository);

            return userDetails;
        }

        // 해당 username 의 user 가 없다면?
        // UsernameNotFoundException 을 throw 해주어야 한다.
        throw new UsernameNotFoundException(username);
    }
}
