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

package com.fortify.cli.fod.release.cli.cmd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fortify.cli.common.output.cli.cmd.unirest.IUnirestJsonNodeSupplier;
import com.fortify.cli.common.output.spi.transform.IActionCommandResultSupplier;
import com.fortify.cli.common.output.spi.transform.IRecordTransformer;
import com.fortify.cli.fod.output.cli.AbstractFoDOutputCommand;
import com.fortify.cli.fod.output.mixin.FoDOutputHelperMixins;
import com.fortify.cli.fod.release.cli.mixin.FoDAppRelResolverMixin;
import com.fortify.cli.fod.release.helper.FoDAppRelDescriptor;
import com.fortify.cli.fod.release.helper.FoDAppRelHelper;
import com.fortify.cli.fod.rest.FoDUrls;

import kong.unirest.UnirestInstance;
import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

@Command(name = FoDOutputHelperMixins.Delete.CMD_NAME)
public class FoDAppRelDeleteCommand extends AbstractFoDOutputCommand implements IUnirestJsonNodeSupplier, IRecordTransformer, IActionCommandResultSupplier {
    @Getter @Mixin private FoDOutputHelperMixins.Delete outputHelper;
    @Mixin private FoDAppRelResolverMixin.PositionalParameter appRelResolver;

    @Override
    public JsonNode getJsonNode(UnirestInstance unirest) {
        FoDAppRelDescriptor appRelDescriptor = appRelResolver.getAppRelDescriptor(unirest);
        unirest.delete(FoDUrls.RELEASE)
                .routeParam("relId", String.valueOf(appRelDescriptor.getReleaseId()))
                .asObject(JsonNode.class).getBody();
        return appRelDescriptor.asObjectNode();
    }

    @Override
    public JsonNode transformRecord(JsonNode record) {
        return FoDAppRelHelper.renameFields(record);
    }

    @Override
    public String getActionCommandResult() {
        return "DELETED";
    }

    @Override
    public boolean isSingular() {
        return true;
    }
}
