/*******************************************************************************
 * (c) Copyright 2021 Micro Focus or one of its affiliates
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including without 
 * limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to 
 * whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY 
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 ******************************************************************************/
package com.fortify.cli.ssc.session.helper;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fortify.cli.common.rest.unirest.config.IUrlConfig;
import com.fortify.cli.common.rest.unirest.config.IUserCredentialsConfig;
import com.fortify.cli.common.session.helper.AbstractSessionDescriptorWithSingleUrlConfig;
import com.fortify.cli.common.session.helper.SessionSummary;
import com.fortify.cli.common.util.StringUtils;
import com.fortify.cli.ssc.entity.token.helper.SSCTokenCreateRequest;
import com.fortify.cli.ssc.entity.token.helper.SSCTokenCreateResponse;
import com.fortify.cli.ssc.entity.token.helper.SSCTokenCreateResponse.SSCTokenData;
import com.fortify.cli.ssc.entity.token.helper.SSCTokenHelper;

import io.micronaut.core.annotation.ReflectiveAccess;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)  @ReflectiveAccess @JsonIgnoreProperties(ignoreUnknown = true)
public class SSCSessionDescriptor extends AbstractSessionDescriptorWithSingleUrlConfig {
    private char[] predefinedToken;
    private SSCTokenCreateResponse cachedTokenResponse;
    
    protected SSCSessionDescriptor() {}
    
    public SSCSessionDescriptor(IUrlConfig urlConfig, ISSCCredentialsConfig credentialsConfig) {
        super(urlConfig);
        this.predefinedToken = credentialsConfig.getPredefinedToken();
        this.cachedTokenResponse = generateToken(urlConfig, credentialsConfig);
    }
    
    @JsonIgnore
    public void logout(IUserCredentialsConfig userCredentialsConfig) {
        if ( cachedTokenResponse!=null && userCredentialsConfig!=null ) {
            SSCTokenHelper.deleteTokensById(getUrlConfig(), userCredentialsConfig, getTokenId());
        }
    }

    @JsonIgnore 
    public final char[] getActiveToken() {
        if ( hasActiveCachedTokenResponse() ) {
            return getCachedTokenResponseData().getToken();
        } else {
            return predefinedToken;
        }
    }
    
    @JsonIgnore
    public final boolean hasActiveCachedTokenResponse() {
        return getCachedTokenResponseData()!=null && getCachedTokenResponseData().getTerminalDate().after(new Date()); 
    }
    
    @JsonIgnore
    public Date getExpiryDate() {
        Date sessionExpiryDate = SessionSummary.EXPIRES_UNKNOWN;
        if ( getCachedTokenTerminalDate()!=null ) {
            sessionExpiryDate = getCachedTokenTerminalDate();
        }
        return sessionExpiryDate;
    }
    
    @JsonIgnore
    protected SSCTokenCreateResponse generateToken(IUrlConfig urlConfig, ISSCCredentialsConfig credentialsConfig) {
        if ( credentialsConfig.getPredefinedToken()==null ) {
            ISSCUserCredentialsConfig uc = credentialsConfig.getUserCredentialsConfig();
            if ( uc!=null && StringUtils.isNotBlank(uc.getUser()) && uc.getPassword()!=null ) { 
                SSCTokenCreateRequest tokenCreateRequest = SSCTokenCreateRequest.builder()
                    .description("Auto-generated by fcli session login command")
                    .terminalDate(uc.getExpiresAt())
                    .type("UnifiedLoginToken")
                    .build();
                return SSCTokenHelper.createToken(urlConfig, uc, tokenCreateRequest, SSCTokenCreateResponse.class);
            }
        }
        return null;
    }
    
    @JsonIgnore 
    private final String getTokenId() {
        if ( hasActiveCachedTokenResponse() ) {
            return getCachedTokenResponseData().getId();
        } else {
            return null;
        }
    }
    
    @JsonIgnore
    private Date getCachedTokenTerminalDate() {
        return getCachedTokenResponseData()==null ? null : getCachedTokenResponseData().getTerminalDate();
    }
    
    @JsonIgnore
    private SSCTokenData getCachedTokenResponseData() {
        return cachedTokenResponse==null || cachedTokenResponse.getData()==null 
                ? null
                : cachedTokenResponse.getData();
    }
    
    @JsonIgnore @Override
    public String getType() {
        return SSCSessionHelper.instance().getType();
    }
}