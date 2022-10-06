package com.fortify.cli.sc_dast.rest.cli.mixin;

import com.fortify.cli.common.output.cli.mixin.spi.IRequestUpdater;
import com.fortify.cli.common.util.StringUtils;

import kong.unirest.HttpRequest;
import lombok.Getter;
import picocli.CommandLine.Option;

public class SCDastSearchTextMixin implements IRequestUpdater {
    @Option(names = {"-t","--search-text"})
    @Getter private String searchText;
    
    public HttpRequest<?> updateRequest(HttpRequest<?> request) {
        return StringUtils.isBlank(searchText) 
                ? request
                : request.queryString("searchText", searchText);
    }
}
