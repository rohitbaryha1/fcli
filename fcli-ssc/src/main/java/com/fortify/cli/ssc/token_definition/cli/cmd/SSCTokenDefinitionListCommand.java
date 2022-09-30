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
package com.fortify.cli.ssc.token_definition.cli.cmd;

import com.fortify.cli.ssc.rest.SSCUrls;
import com.fortify.cli.ssc.rest.cli.cmd.AbstractSSCListCommand;
import com.fortify.cli.ssc.rest.query.SSCOutputQueryQParamGenerator;
import com.fortify.cli.ssc.rest.query.SSCQParamValueGenerators;

import io.micronaut.core.annotation.ReflectiveAccess;
import kong.unirest.GetRequest;
import kong.unirest.UnirestInstance;
import picocli.CommandLine.Command;

@ReflectiveAccess
@Command(name = "list")
public class SSCTokenDefinitionListCommand extends AbstractSSCListCommand {
    @Override
    protected SSCOutputQueryQParamGenerator getQParamGenerator() {
        return new SSCOutputQueryQParamGenerator()
                .add("type", SSCQParamValueGenerators::wrapInQuotes)
                .add("maxDaysToLive", SSCQParamValueGenerators::plain)
                .add("maxUsages", SSCQParamValueGenerators::plain)
                .add("capabilityDescription", SSCQParamValueGenerators::wrapInQuotes);
    }

    protected GetRequest generateRequest(UnirestInstance unirest) {
        return unirest.get(SSCUrls.TOKEN_DEFINITIONS).queryString("limit", "-1");
    }
}