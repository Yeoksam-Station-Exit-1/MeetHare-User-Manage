package yeoksamstationexit1.usermanage.global.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import yeoksamstationexit1.usermanage.global.jwt.JwtService;
import yeoksamstationexit1.usermanage.user.Role;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtService jwtService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    log.info("OAuth2 Login 성공!");
    try {

      CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

      // User의 Role이 GUEST일 경우 처음 요청한 회원이므로 회원가입 페이지로 리다이렉트
      if (oAuth2User.getRole() == Role.USER) {

        String accessToken = jwtService.createAccessTokenWithNickName(oAuth2User.getEmail(),oAuth2User.getNickName(),oAuth2User.getUserId());

        
        String referer = request.getHeader("Referer");

        if (referer.equals("http://localhost:3000/") ) {

          ResponseCookie cookie = ResponseCookie.from("Bearer", accessToken)
                  .path("/")
                  
                  .build();

          response.addHeader("Set-Cookie", cookie.toString());
          response.sendRedirect("http://localhost:3000"); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트
        }
        else{


          ResponseCookie cookie = ResponseCookie.from("Bearer", accessToken)
                  .path("/")
                  .secure(true)
                  .domain("meethare.site")
                  .build();

          response.addHeader("Set-Cookie", cookie.toString());
          response.sendRedirect("https://meethare.site/");
        }
      } else {

        loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성
      }
    } catch (Exception e) {

      log.info(e.toString());
    }

  }

  // 로그인이 성공했을 경우, Access/Refresh Token 발급 후 갱신
  private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {

    String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
    String refreshToken = jwtService.createRefreshToken();
    response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
    response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

    jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
    jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
  }
}