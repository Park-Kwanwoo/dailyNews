package dev.park.dailynews.service;

import dev.park.dailynews.domain.social.KakaoUserInfo;
import dev.park.dailynews.domain.social.SocialProvider;
import dev.park.dailynews.domain.social.SocialUserInfo;
import dev.park.dailynews.domain.user.User;
import dev.park.dailynews.dto.response.sosical.KakaoLoginParams;
import dev.park.dailynews.dto.response.sosical.SocialLoginParams;
import dev.park.dailynews.model.UserContext;
import dev.park.dailynews.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class SocialLoginServiceTest {

    @InjectMocks
    private SocialLoginService socialLoginService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private SocialClientService socialClientService;

    @Test
    @DisplayName("카카오_소셜_로그인_토큰_정보_받아오기")
    void GET_KAKAO_SOCIAL_LOGIN_TOKEN_INFO() {

        String code = "test-auth-code";
        SocialLoginParams params = new KakaoLoginParams(code);
        SocialUserInfo userInfo = new KakaoUserInfo(new KakaoUserInfo.KakaoAccount(
                "kakao@auth.com",
                new KakaoUserInfo.KakaoAccount.Profile("테스터")
        ));

        // given
        given(socialClientService.request(params)).willReturn(userInfo);
        given(userRepository.findByEmail("kakao@auth.com")).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willAnswer(i -> i.getArgument(0));

        // when
        UserContext result = socialLoginService.login(params);

        // then
        assertEquals("kakao@auth.com", result.getEmail());
        assertEquals("kakao@auth.com", result.getEmail());
    }
}
