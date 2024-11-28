package com.lec.spring.config;

import com.lec.spring.domain.Authority;
import com.lec.spring.domain.User;
import com.lec.spring.repository.AuthorityRepository;
import com.lec.spring.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

//시큐리티가 /user/login (POST) 주소요청이 오면 낚아채서 로그인을 진행시킨다.
//로그인(인증) 진행이 완료되면 '시큐리티 session' 에 넣어주게 된다.
//우리가 익히 알고 있는 같은 session 공간이긴 한데..
//시큐리티가 자신이 사용하기 위한 공간을 가집니다.
//=> Security ContextHolder 라는 키값에다가 session 정보를 저장합니다.
//여기에 들어갈수 있는 객체는 Authentication 객체이어야 한다.
//Authentication 안에 User 정보가 있어야 됨.
//User 정보 객체는 ==> UserDetails 타입 객체이어야 한다.


//따라서 로그인한 User 정보를 꺼내려면
//Security Session 에서
//   => Authentication 객체를 꺼내고, 그 안에서
//        => UserDetails 정보를 꺼내면 된다.

public class PrincipalDetails implements UserDetails, OAuth2User {

    private AuthorityRepository authorityRepository;

    public void setAuthorityRepository(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }


    //로그인한 사용자 정보 가져오기
    private User user;

    public User getUser() {
        return user;
    }

    //일반 로그인용 생성자
    public PrincipalDetails(User user) {
        System.out.println("UserDetails(user) 생성");
        this.user = user;
    }

    //OAuth 로그인용 생성자
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        System.out.println("UserDetails(user, attributes) 생성");
        System.out.println("""
           UserDetails(user, oauth attributes) 생성:
               user: %s
               attributes: %s
           """.formatted(user, attributes));
        this.user = user;
        this.attributes = attributes;
    }

    // 해당 User 의 '권한(들)'을 리턴
    // 현재 로그인한 사용자의 권한정보가 필요할때마다 호출된다. 혹은 필요할때마다 직접 호출해 사용할수도 있다
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        System.out.println("getAuthorities() 호출. 수시로 호출될 것");
        Collection<GrantedAuthority> collect = new ArrayList<>();
        List<Authority> list = authorityRepository.findByUser(user);

        for (Authority auth : list) {
            collect.add(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return auth.getName();
                }

                //학습목적 구문, 타임리프 등에서 활용. 이거 아니면 위엣 놈만 써서 람다식이 맞음
                @Override
                public String toString() {
                    return auth.getName();
                }
            });
        }

        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    //만료된 계정인가? t 리턴 시 로긘 가능
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //활동정지계정 확인
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //사용자의 인증 자격 증명(비밀번호 등)이 만료되었는가(갱신 필요)?
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //휴면계정인가?
    @Override
    public boolean isEnabled() {
        return true;
    }

    //_______________________________________________
    //OAuth2에서 구현할 메소드

    private Map<String, Object> attributes;   // <- OAuth2User 의 getAttributes() 값

    @Override//여기선 안씀 ㅅㄱ
    public String getName() {
        return "";
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }
}
