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
package com.fortify.cli.util.entity.ncd_report.config;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.formkiq.graalvm.annotations.Reflectable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class holds the various source-specific source configurations,
 * together with a global {@link #includeForks} setting.
 * @author rsenden
 *
 */
@Reflectable @NoArgsConstructor
@Data
public class NcdReportSourcesConfig {
    private Optional<Boolean> includeForks = Optional.empty();
    private Optional<NcdReportGitHubSourceConfig[]> github = Optional.empty();
    private Optional<NcdReportGitLabSourceConfig[]> gitlab = Optional.empty();
    
    public final List<INcdReportSourceConfig> getSourceConfigs() {
        return Stream.of(github, gitlab)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(INcdReportSourceConfig[].class::cast)
                .flatMap(Stream::of)
                .collect(Collectors.toList());
    }
}
