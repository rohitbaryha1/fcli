package com.fortify.cli.ssc.appversion.cli.cmd;

import com.fortify.cli.common.cli.cmd.AbstractFortifyCLICommand;
import com.fortify.cli.common.variable.PredefinedVariable;

import picocli.CommandLine.Command;

@Command(
        name = "appversion",
        subcommands = {
            SSCAppVersionCreateCommand.class,
            SSCAppVersionDeleteCommand.class,
            SSCAppVersionGetCommand.class,
            SSCAppVersionListCommand.class,
            SSCAppVersionUpdateCommand.class
        }
)
@PredefinedVariable(name = "_ssc_currentAppVersion", field = "id")
public class SSCAppVersionCommands extends AbstractFortifyCLICommand {
}