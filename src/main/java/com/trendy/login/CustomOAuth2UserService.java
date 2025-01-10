package com.trendy.login;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private SocialAccountRepository socialAccountRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String providerStr = userRequest.getClientRegistration().getRegistrationId();
        String email = null;
        String socialId = null;
        String profileImageUrl = null;

        logger.info("Provider: {}", providerStr);
        logger.debug("Attributes: {}", oauth2User.getAttributes());

        try {
            switch (providerStr) {
                case "google":
                    email = oauth2User.getAttribute("email");
                    socialId = oauth2User.getAttribute("sub");
                    profileImageUrl = oauth2User.getAttribute("picture");
                    break;

                case "naver":
                    @SuppressWarnings("unchecked")
                    Map<String, Object> response = (Map<String, Object>) oauth2User.getAttribute("response");
                    if (response != null) {
                        email = (String) response.get("email");
                        socialId = (String) response.get("id");
                        profileImageUrl = (String) response.get("profile_image");
                    }
                    break;

                case "kakao":
                    @SuppressWarnings("unchecked")
                    Map<String, Object> kakaoAccount = (Map<String, Object>) oauth2User.getAttribute("kakao_account");
                    if (kakaoAccount != null) {
                        email = (String) kakaoAccount.get("email");
                        socialId = String.valueOf(oauth2User.getAttribute("id"));
                        @SuppressWarnings("unchecked")
                        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                        if (profile != null) {
                            profileImageUrl = (String) profile.get("profile_image_url");
                        }
                    }
                    break;

                default:
                    throw new OAuth2AuthenticationException("Unsupported provider: " + providerStr);
            }

            if (email == null || socialId == null || providerStr == null) {
                throw new OAuth2AuthenticationException("Required fields are missing (email, socialId, provider)");
            }

            Optional<SocialAccount> existingAccount = socialAccountRepository.findBySocialIdAndProvider(
                socialId, Provider.valueOf(providerStr.toUpperCase()));

            if (existingAccount.isEmpty()) {
                SocialAccount socialAccount = new SocialAccount();
                socialAccount.setEmail(email);
                socialAccount.setProvider(Provider.valueOf(providerStr.toUpperCase()));
                socialAccount.setSocialId(socialId);
                socialAccount.setProfileImageUrl(profileImageUrl);
                socialAccountRepository.save(socialAccount);
                logger.info("New account created with email: {}", email);
            } else {
                logger.info("Account already exists for socialId: {}", socialId);
            }

            return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of(
                    "email", email,
                    "socialId", socialId,
                    "profileImageUrl", profileImageUrl
                ),
                "email"
            );

        } catch (Exception e) {
            logger.error("OAuth2 authentication error: {}", e.getMessage(), e);
            throw new OAuth2AuthenticationException("Authentication failed: " + e.getMessage());
        }
    }
}
