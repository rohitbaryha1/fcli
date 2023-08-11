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
package com.fortify.cli.ssc.appversion_user.cli.cmd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fortify.cli.common.output.cli.mixin.OutputHelperMixins;
import com.fortify.cli.common.output.transform.IActionCommandResultSupplier;
import com.fortify.cli.ssc._common.output.cli.cmd.AbstractSSCJsonNodeOutputCommand;
import com.fortify.cli.ssc.appversion.cli.mixin.SSCAppVersionResolverMixin;
import com.fortify.cli.ssc.appversion_user.cli.mixin.SSCAppVersionUserMixin;
import com.fortify.cli.ssc.appversion_user.helper.SSCAppVersionUserUpdateBuilder;
import com.fortify.cli.ssc.appversion_user.helper.SSCAppVersionUserUpdateBuilder.SSCAppVersionAuthEntitiesUpdater;

import kong.unirest.UnirestInstance;
import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

@Command(name = OutputHelperMixins.Add.CMD_NAME)
public class SSCAppVersionUserAddCommand extends AbstractSSCJsonNodeOutputCommand implements IActionCommandResultSupplier {
    @Getter @Mixin private OutputHelperMixins.Add outputHelper;
    @Mixin private SSCAppVersionUserMixin.RequiredPositionalParameter authEntityMixin;
    @Mixin private SSCAppVersionResolverMixin.RequiredOption parentResolver;
    @Option(names="--allow-multi-match", defaultValue = "false")
    private boolean allowMultiMatch;
    
    @Override
    public JsonNode getJsonNode(UnirestInstance unirest) {
        String applicationVersionId = parentResolver.getAppVersionId(unirest);
        SSCAppVersionAuthEntitiesUpdater updater = new SSCAppVersionUserUpdateBuilder(unirest)
                .add(allowMultiMatch, authEntityMixin.getAuthEntitySpecs())
                .build(applicationVersionId);
        updater.getUpdateRequest().asObject(JsonNode.class).getBody();
        return updater.getAuthEntitiesToAdd();
    }
    
    @Override
    public String getActionCommandResult() {
        return "ADDED";
    }
    
    @Override
    public boolean isSingular() {
        return false;
    }
}