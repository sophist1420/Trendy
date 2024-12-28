package com.trendy.login;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Debugging: 응답 데이터 출력
        System.out.println("OAuth2User Attributes: " + attributes);

        // 데이터 추출
        String name = extractName(attributes, registrationId);
        String email = extractEmail(attributes, registrationId);
        String profileImage = extractProfileImage(attributes, registrationId);

        // 기본값 설정
        name = (name == null || name.isEmpty()) ? "Unknown User" : name;
        email = (email == null || email.isEmpty() || "unknown@example.com".equals(email))
                ? generateUniqueEmail()
                : email;
        profileImage = (profileImage == null || profileImage.isEmpty())
                ? "https://example.com/default-profile.jpg"
                : profileImage;

        System.out.println("Extracted Name: " + name);
        System.out.println("Extracted Email: " + email);
        System.out.println("Extracted Profile Image: " + profileImage);

        saveOrUpdateUser(name, email, profileImage);
        return oAuth2User;
    }

    private String extractName(Map<String, Object> attributes, String registrationId) {
        if ("google".equals(registrationId)) {
            return (String) attributes.get("name");
        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount == null) {
                System.out.println("Error: kakaoAccount is null");
                return "Unknown User";
            }

            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile == null) {
                System.out.println("Error: profile is null");
                return "Unknown User";
            }

            return (String) profile.get("nickname");
        }
        return "Unknown User";
    }

    private String extractEmail(Map<String, Object> attributes, String registrationId) {
        if ("google".equals(registrationId)) {
            return (String) attributes.get("email");
        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount == null) {
                System.out.println("Error: kakaoAccount is null");
                return null;
            }

            return (String) kakaoAccount.get("email");
        }
        return null;
    }

    private String extractProfileImage(Map<String, Object> attributes, String registrationId) {
        if ("google".equals(registrationId)) {
            return (String) attributes.get("picture");
        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount == null) {
                System.out.println("Error: kakaoAccount is null");
                return null;
            }

            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile == null) {
                System.out.println("Error: profile is null");
                return null;
            }

            return (String) profile.get("profile_image_url");
        }
        return null;
    }

    private void saveOrUpdateUser(String name, String email, String profileImage) {
        if (name == null || name.isEmpty()) {
            System.out.println("Error: Invalid username. Skipping saveOrUpdateUser.");
            return;
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            user = new User();
            user.setUsername(name);
            user.setEmail(email);
            user.setProfileImageUrl(profileImage);
            userRepository.save(user);
            System.out.println("New User Created: " + user.getUsername());
        } else {
            user.setUsername(name); // 이름 업데이트
            user.setProfileImageUrl(profileImage);
            userRepository.save(user);
            System.out.println("Existing User Updated: " + user.getUsername());
        }
    }

    private String generateUniqueEmail() {
        return "user-" + UUID.randomUUID() + "@example.com";
    }
}
