/*******************************************************************************
 * Copyright 2021, 2022 Open Text.
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
package com.fortify.cli.tool.bugtracker_utility.cli.cmd;

import com.fortify.cli.common.cli.cmd.AbstractContainerCommand;

import picocli.CommandLine.Command;

@Command(
        name = ToolBugTrackerUtilityCommands.TOOL_NAME,
        aliases = {"bugtrackerutility"},
        subcommands = {
                ToolBugTrackerUtilityInstallCommand.class,
                ToolBugTrackerUtilityListCommand.class,
                ToolBugTrackerUtilityUninstallCommand.class
        }

)
public class ToolBugTrackerUtilityCommands extends AbstractContainerCommand {
    static final String TOOL_NAME = "bugtracker-utility";
}