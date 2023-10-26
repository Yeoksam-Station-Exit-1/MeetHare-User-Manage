package yeoksamstationexit1.usermanage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yeoksamstationexit1.usermanage.user.dto.dayCalendarDTO;
//import yeoksamstationexit1.usermanage.user.entity.FixCalendarEntity;
//import yeoksamstationexit1.usermanage.user.repository.FixCalendarRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
//  private final FixCalendarRepository fixCalendarRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * User 생성
   * JPA Repository의 save Method를 사용하여 객체를 생성
   * Entity인 Model 객체에 @Id로 설정한 키 값이 없을 경우 해당하는 데이터를 추가
   * 만약 추가하려는 Entity인 Model 객체에 @Id 값이 이미 존재하면 갱신되기 때문에
   * 아래와 같이 추가하고자 하는 User가 존재하는지 체크하는 로직을 추가
   *
   * @param model
   * @return
   */
  public void signUp(UserSignUpDto userSignUpDto) throws Exception {

    if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
      throw new Exception("이미 존재하는 이메일입니다.");
    }

    if (userRepository.findByNickname(userSignUpDto.getNickname()).isPresent()) {
      throw new Exception("이미 존재하는 닉네임입니다.");
    }

    UserEntity user = UserEntity.builder()
            .email(userSignUpDto.getEmail())
            .password(userSignUpDto.getPassword())
            .nickname(userSignUpDto.getNickname())
            .role(Role.USER)
            .build();

    user.passwordEncode(passwordEncoder);
    userRepository.save(user);
  }

  /**
   * User 수정
   * JPA Repository의 save Method를 사용하여 객체를 갱신
   * Entity인 Model 객체에 @Id로 설정한 키 값이 존재할 경우 해당하는 데이터를 갱신
   * 만약 수정하려는 Entity인 Model 객체에 @Id 값이 존재하지 않으면 데이터가 추가되기 때문에
   * 아래와 같이 갱신하고자 하는 User가 존재하는지 체크하는 로직을 추가
   *
   * @param model
   * @return
   */
  public UserEntity updateUserInfo(UserDetails token, UserUpdateDTO updateData) {

    UserEntity updatedUser = null;

    try {

      if (updateData.isUserUpdateEmpty())
        throw new Exception("Required info is not qualified");

      UserEntity existUser = getUser(token.getUsername());

      existUser.setNickname(updateData.getNickname());
      existUser.setBirthDay(updateData.getBirthDay());
      existUser.setPhoneNum(updateData.getPhoneNum());
      existUser.setHome(updateData.getHome());


      if (!existUser.isUserInfoEmpty())
        existUser.authorizeUser();

      // if (!ObjectUtils.isEmpty(existUser))
      // updatedUser = userRepository.save(model);

    } catch (Exception e) {

      log.info("[Fail] e: " + e.toString());
    }

    return updatedUser;
  }


//  public HttpStatus setCalendar(UserDetails token, dayCalendarDTO daycalendardto) {
//
//
//    try {
//
//
//      UserEntity existUser = getUser(token.getUsername());
//      FixCalendarEntity calendar = new FixCalendarEntity();
//      calendar.setUser(existUser);
//      calendar.setDay(daycalendardto.getDay());
//      calendar.setTime(daycalendardto.getTime());
//
//      fixCalendarRepository.save(calendar);
//
//    }
//    catch (Exception e){
//      log.info("캘린더 저장 실패");
//    }
//
//
//
//
//    return HttpStatus.OK;
//  }



  /**
   * User List 조회
   * JPA Repository의 findAll Method를 사용하여 전체 User를 조회
   *
   * @return
   */
  public List<UserEntity> getUsers() {
    return userRepository.findAll();
  }

  /**
   * Id에 해당하는 User 조회
   * JPA Repository의 findBy Method를 사용하여 특정 User를 조회
   * find 메소드는 NULL 값일 수도 있으므로 Optional<T>를 반환하지만,
   * Optional 객체의 get() 메소드를 통해 Entity로 변환해서 반환함.
   *
   * @param id
   * @return
   */
  public UserEntity getUser(String email) {

    return userRepository.findByEmail(email).get();
  }

  /**
   * Id에 해당하는 User 삭제
   * JPA Repository의 deleteBy Method를 사용하여 특정 User를 삭제
   *
   * @param id
   */
  public void deleteUser(String id) {
    userRepository.deleteById(id);
  }


}