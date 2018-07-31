package com.labrador.authservice.config;

import com.labrador.authservice.entity.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class CustomTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> additionalInfo = new HashMap<>();
        enhanceOrganization(additionalInfo, authentication);
        enhanceUserInfo(additionalInfo, authentication);
        ((DefaultOAuth2AccessToken)accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
    private void enhanceOrganization(Map<String, Object> additionalInfo,  OAuth2Authentication authentication){
        if (authentication.getName().startsWith("org")){
            additionalInfo.put("organization", "ORG");
        }else{
            additionalInfo.put("organization", "");
        }
    }

    private void enhanceUserInfo(Map<String, Object> additionalInfo,  OAuth2Authentication authentication){
        String grantType = authentication.getOAuth2Request().getGrantType();
        if (grantType != null && !grantType.equalsIgnoreCase("client_credentials")){
            User user = (User)authentication.getPrincipal();
            additionalInfo.put("displayName", user.getDisplayName());
        }
    }
}
