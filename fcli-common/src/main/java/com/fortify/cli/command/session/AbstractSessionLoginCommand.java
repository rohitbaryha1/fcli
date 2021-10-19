/*******************************************************************************
 * (c) Copyright 2020 Micro Focus or one of its affiliates
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
package com.fortify.cli.command.session;

import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortify.cli.util.FcliHomeHelper;

import jakarta.inject.Inject;
import kong.unirest.UnirestInstance;
import lombok.Getter;
import lombok.SneakyThrows;
import picocli.CommandLine.Mixin;

public abstract class AbstractSessionLoginCommand implements Runnable {
	@Getter private ObjectMapper objectMapper;
	
	@Inject
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	@Mixin
	@Getter private LoginSessionProducerMixin loginSessionProducerMixin;
	
	@Override @SneakyThrows
	public final void run() {
		String loginSessionType = getLoginSessionType();
		String connectionId = loginSessionProducerMixin.getConnectionId(loginSessionType);
		Object loginData = login();
		String loginDataJson = objectMapper.writeValueAsString(loginData);
		System.out.println(String.format("Creating login session %s: %s", connectionId, loginDataJson));
		FcliHomeHelper.saveFile(Paths.get("loginSessions", loginSessionType, connectionId), loginDataJson);
	}
	
	protected UnirestInstance getUnirestInstance() {
		return loginSessionProducerMixin.getUnirestInstance(getLoginSessionType());
	}
	
	protected abstract String getLoginSessionType();
	protected abstract Object login();
}
