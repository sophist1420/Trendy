package com.trendy.login;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final SocialAccountRepository socialAccountRepository;
    private final UserRepository userRepository;

    public CustomOAuth2UserService(SocialAccountRepository socialAccountRepository, UserRepository userRepository) {
        this.socialAccountRepository = socialAccountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> originalAttributes = oAuth2User.getAttributes();

        Map<String, Object> attributes = new HashMap<>(originalAttributes);

        if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            if (response != null) {
                attributes.putAll(response);
            }
        }

        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount != null) {
                attributes.putAll(kakaoAccount);
                attributes.put("nickname", ((Map<String, Object>) kakaoAccount.get("profile")).get("nickname"));
                attributes.put("profile_image", ((Map<String, Object>) kakaoAccount.get("profile")).get("profile_image_url"));
            }
        }

        String socialId = extractSocialId(attributes, registrationId);

        System.out.println("[DEBUG] Extracted Social ID: " + socialId);

        Optional<SocialAccount> socialAccountOpt = socialAccountRepository.findBySocialIdAndProvider(
            socialId, Provider.valueOf(registrationId.toUpperCase()));

        if (socialAccountOpt.isEmpty()) {
            SocialAccount socialAccount = new SocialAccount();
            socialAccount.setProvider(Provider.valueOf(registrationId.toUpperCase()));
            socialAccount.setSocialId(socialId);
            socialAccountRepository.save(socialAccount);

            System.out.println("[DEBUG] New SocialAccount created: " + socialAccount);
        } else {
            System.out.println("[DEBUG] SocialAccount already exists: " + socialAccountOpt.get());
        }

        return new DefaultOAuth2User(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            determineAttributeKey(registrationId)
        );
    }

    private String extractSocialId(Map<String, Object> attributes, String registrationId) {
        if ("google".equals(registrationId)) {
            return (String) attributes.get("sub");
        } else if ("kakao".equals(registrationId)) {
            return String.valueOf(attributes.get("id"));
        } else if ("naver".equals(registrationId)) {
            return (String) attributes.get("id");
        }
        throw new IllegalArgumentException("Unsupported registrationId: " + registrationId);
    }

    private String determineAttributeKey(String registrationId) {
        if ("google".equals(registrationId)) {
            return "sub";
        } else if ("kakao".equals(registrationId)) {
            return "id";
        } else if ("naver".equals(registrationId)) {
            return "id";
        }
        throw new IllegalArgumentException("Unsupported registrationId: " + registrationId);
    }
}
