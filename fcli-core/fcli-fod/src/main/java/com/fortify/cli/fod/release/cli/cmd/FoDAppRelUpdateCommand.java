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

package com.fortify.cli.fod.release.cli.cmd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fortify.cli.common.output.cli.mixin.OutputHelperMixins;
import com.fortify.cli.common.output.transform.IActionCommandResultSupplier;
import com.fortify.cli.common.output.transform.IRecordTransformer;
import com.fortify.cli.common.util.StringUtils;
import com.fortify.cli.fod._common.output.cli.AbstractFoDJsonNodeOutputCommand;
import com.fortify.cli.fod.app.cli.mixin.FoDSdlcStatusTypeOptions;
import com.fortify.cli.fod.microservice.helper.FoDAppMicroserviceDescriptor;
import com.fortify.cli.fod.microservice.helper.FoDAppMicroserviceHelper;
import com.fortify.cli.fod.release.cli.mixin.FoDAppRelResolverMixin;
import com.fortify.cli.fod.release.helper.FoDAppRelDescriptor;
import com.fortify.cli.fod.release.helper.FoDAppRelHelper;
import com.fortify.cli.fod.release.helper.FoDAppRelUpdateRequest;

import kong.unirest.UnirestInstance;
import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;


@Command(name = OutputHelperMixins.Update.CMD_NAME)
public class FoDAppRelUpdateCommand extends AbstractFoDJsonNodeOutputCommand implements IRecordTransformer, IActionCommandResultSupplier {
    @Getter @Mixin private OutputHelperMixins.Update outputHelper;
    @Mixin private FoDAppRelResolverMixin.PositionalParameter appRelResolver;
    @Spec CommandSpec spec;
    //ResourceBundle bundle = ResourceBundle.getBundle("com.fortify.cli.fod.i18n.FoDMessages");
    @Option(names = {"--name", "-n"})
    private String releaseName;

    @Option(names = {"--description", "-d"})
    private String description;

    @Option(names = {"--owner"})
    private String releaseOwner;

    @Option(names = {"--microservice"})
    private String microserviceNameOrId;

    @Mixin
    private FoDSdlcStatusTypeOptions.RequiredOption sdlcStatus;

    @Override
    public JsonNode getJsonNode(UnirestInstance unirest) {

        int microServiceId = 0;
        FoDAppRelDescriptor appRelDescriptor = appRelResolver.getAppRelDescriptor(unirest);
        if (microserviceNameOrId != null && !microserviceNameOrId.isEmpty()) {
            FoDAppMicroserviceDescriptor descriptor = FoDAppMicroserviceHelper.getAppMicroserviceDescriptor(unirest,
                    appRelDescriptor.getApplicationName(), microserviceNameOrId, true);
            microServiceId = descriptor.getMicroserviceId();
        }

        FoDSdlcStatusTypeOptions.FoDSdlcStatusType sdlcStatusTypeNew = sdlcStatus.getSdlcStatusType();

        FoDAppRelUpdateRequest appRelUpdateRequest = FoDAppRelUpdateRequest.builder()
                .releaseName(StringUtils.isNotBlank(releaseName) ? releaseName : appRelDescriptor.getReleaseName())
                .releaseDescription(StringUtils.isNotBlank(description) ? description : appRelDescriptor.getReleaseDescription())
                .ownerId(StringUtils.isNotBlank(releaseOwner) ? Integer.valueOf(releaseOwner) : appRelDescriptor.getOwnerId())
                .microserviceId(microServiceId)
                .sdlcStatusType(sdlcStatusTypeNew != null ? String.valueOf(sdlcStatusTypeNew) : appRelDescriptor.getSdlcStatusType()).build();

        return FoDAppRelHelper.updateAppRel(unirest, appRelDescriptor.getReleaseId(), appRelUpdateRequest).asJsonNode();
    }
    @Override
    public JsonNode transformRecord(JsonNode record) {
        return FoDAppRelHelper.renameFields(record);
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