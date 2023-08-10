/*******************************************************************************
 * Copyright 2021, 2023 Open Text.
 *
 * The only warranties for products and services of Open Text 
 * and its affiliates and licensors ("Open Text") are as may 
 * be set forth in the express warranty statements accompanying 
 * such products and services. Nothing herein should be construed 
 * as constituting an additional warranty. Open Text shall not be 
 * liable for technical or editorial errors or omissions contained 
 * herein. The information contained herein is subject to change 
 * without notice.
 *******************************************************************************/
package com.fortify.cli.ssc.app.cli.cmd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fortify.cli.common.output.cli.mixin.OutputHelperMixins;
import com.fortify.cli.common.output.transform.IActionCommandResultSupplier;
import com.fortify.cli.common.util.StringUtils;
import com.fortify.cli.ssc._common.output.cli.cmd.AbstractSSCJsonNodeOutputCommand;
import com.fortify.cli.ssc._common.rest.SSCUrls;
import com.fortify.cli.ssc.app.cli.mixin.SSCAppResolverMixin;
import com.fortify.cli.ssc.app.helper.SSCAppDescriptor;
import com.fortify.cli.ssc.app.helper.SSCAppHelper;

import kong.unirest.core.UnirestInstance;
import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

@Command(name = OutputHelperMixins.Update.CMD_NAME)
public class SSCAppUpdateCommand extends AbstractSSCJsonNodeOutputCommand implements IActionCommandResultSupplier {
    @Getter @Mixin private OutputHelperMixins.Update outputHelper; 
    @Mixin private SSCAppResolverMixin.PositionalParameter appResolver;
    @Option(names={"--name","-n"}, required = false)
    private String name;
    @Option(names={"--description","-d"}, required = false)
    private String description;
    
    @Override
    public JsonNode getJsonNode(UnirestInstance unirest) {
        SSCAppDescriptor descriptor = appResolver.getAppDescriptor(unirest);
        ObjectNode updateData = (ObjectNode)descriptor.asJsonNode();
        if ( StringUtils.isNotBlank(name) ) { updateData.put("name", name); }
        if ( StringUtils.isNotBlank(description) ) { updateData.put("description", description); }
        unirest.put(SSCUrls.PROJECT(descriptor.getApplicationId()))
                .body(updateData).asObject(JsonNode.class).getBody();
        return SSCAppHelper.getApp(unirest, descriptor.getApplicationId(), true).asJsonNode();
    }

    @Override
    public String getActionCommandResult() {
        return "UPDATED";
    }
    
    @Override
    public boolean isSingular() {
        return true;
    }
}
