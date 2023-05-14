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
package com.fortify.cli.sc_dast.entity.sensor.cli.cmd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fortify.cli.common.output.cli.mixin.OutputHelperMixins;
import com.fortify.cli.sc_dast.entity.sensor.cli.mixin.SCDastSensorResolverMixin;
import com.fortify.cli.sc_dast.entity.sensor.helper.SCDastSensorHelper;
import com.fortify.cli.sc_dast.output.cli.cmd.AbstractSCDastJsonNodeOutputCommand;

import kong.unirest.UnirestInstance;
import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

@Command(name = OutputHelperMixins.Disable.CMD_NAME)
public class SCDastSensorDisableCommand extends AbstractSCDastJsonNodeOutputCommand {
    @Getter @Mixin private OutputHelperMixins.Disable outputHelper;
    @Mixin private SCDastSensorResolverMixin.PositionalParameter sensorResolver;

    @Override
    public JsonNode getJsonNode(UnirestInstance unirest) {
        return SCDastSensorHelper.disableSensor(unirest, sensorResolver.getSensorDescriptor(unirest)).asJsonNode();
    }
    
    @Override
    public boolean isSingular() {
        return true;
    }
}