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
package com.fortify.cli.common.output.cli.mixin;

import com.fortify.cli.common.output.cli.cmd.AbstractOutputCommand;
import com.fortify.cli.common.output.writer.output.standard.StandardOutputConfig;

import lombok.Getter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

/**
 * <p>This class provides standard {@link IOutputHelper} implementations. Individual
 * product modules may provide additional {@link IOutputHelper} implementations as
 * needed, extending from one of these classes as appropriate:</p>
 * <ul>
 * <li>{@link Other}, not providing any standard output format or output writer factory; subclass should provide {@code CMD_NAME} constant, and outputWriterFactory and basicOutputConfig fields with Getter annotations</li>
 * <li>{@link TableWithQuery}, providing query options and default table output format; subclass should provide {@code CMD_NAME} constant</li>
 * <li>{@link TableNoQuery}, providing no query options and default table output format; subclass should provide {@code CMD_NAME} constant</li>
 * </ul>
 *
 * <p>All standard output helper mixins defined in this class are based on {@code TableNoQuery}, except
 * for the {@code List} class which is based on {@code TableWithQuery}.</p>
 *
 * Every command implementation should use the appropriate {@link IOutputHelper}
 * (where {@code CMD_NAME} matches the command name), either from the standard implementations
 * provided in this class, or from the product-specific implementations. Example:</p>
 *
 * <pre>
 * &#64;ReflectiveAccess
 * &#64;Command(name = OutputHelperMixins.List.CMD_NAME)
 * public class SomeListCommand extends AbstractMyProductOutputCommand implements IBaseHttpRequestSupplier {
 *     &#64;Getter &#64;Mixin private OutputHelperMixins.List outputHelper;
 *     ...
 * }
 * </pre>
 *
 * <p>Here, {@code AbstractMyProductOutputCommand} would extend from {@link AbstractOutputCommand},
 * which takes care of displaying the output generated by the command, based on the configured
 * {@link IOutputHelper} {@link Mixin}.</p>
 *
 * @author rsenden
 */
public class OutputHelperMixins {
    public static class Other extends AbstractOutputHelperMixin {}


    public static class TableWithQuery extends Other {
        @Getter @Mixin private OutputWriterWithQueryFactoryMixin outputWriterFactory;
        @Getter private StandardOutputConfig basicOutputConfig = StandardOutputConfig.table();
    }


    public static class TableNoQuery extends Other {
        @Getter @Mixin private StandardOutputWriterFactoryMixin outputWriterFactory;
        @Getter private StandardOutputConfig basicOutputConfig = StandardOutputConfig.table();
    }

    public static class DetailsNoQuery extends Other {
        @Getter @Mixin private StandardOutputWriterFactoryMixin outputWriterFactory;
        @Getter private StandardOutputConfig basicOutputConfig = StandardOutputConfig.details();
    }

    public static class DetailsWithQuery extends Other {
        @Getter @Mixin private OutputWriterWithQueryFactoryMixin outputWriterFactory;
        @Getter private StandardOutputConfig basicOutputConfig = StandardOutputConfig.details();
    }


    public static class Add extends TableNoQuery {
        public static final String CMD_NAME = "add";
    }


    public static class Create extends TableNoQuery {
        public static final String CMD_NAME = "create";
    }
    public static class CreateWithDetailsOutput extends DetailsNoQuery {
        public static final String CMD_NAME = "create";
    }
    
    public static class CreateConfig extends TableNoQuery {
        public static final String CMD_NAME = "create-config";
    }
    
    public static class CreateTemplate extends TableNoQuery {
        public static final String CMD_NAME = "create-template";
    }
    
    public static class CreateTemplateConfig extends TableNoQuery {
        public static final String CMD_NAME = "create-template-config";
    }

    @Command(aliases = {"rm"})
    public static class Delete extends TableNoQuery {
        public static final String CMD_NAME = "delete";
    }
    
    @Command(aliases = {"rm-template", "rmt"})
    public static class DeleteTemplate extends TableNoQuery {
        public static final String CMD_NAME = "delete-template";
    }

    public static class Clear extends TableNoQuery {
        public static final String CMD_NAME = "clear";
    }

    public static class Revoke extends TableNoQuery {
        public static final String CMD_NAME = "revoke";
    }

    @Command(aliases = {"ls"})
    public static class List extends TableWithQuery {
        public static final String CMD_NAME = "list";
    }
    
    @Command(aliases = {"listdef", "lsd"})
    public static class ListDefinitions extends TableWithQuery {
        public static final String CMD_NAME = "list-definitions";
    }
    
    @Command(aliases = {"lst"})
    public static class ListTemplates extends TableWithQuery {
        public static final String CMD_NAME = "list-templates";
    }

    public static class Get extends DetailsNoQuery {
        public static final String CMD_NAME = "get";
    }
    
    @Command(aliases = {"getdef"})
    public static class GetDefinition extends DetailsNoQuery {
        public static final String CMD_NAME = "get-definition";
    }
    
    public static class GetTemplate extends DetailsNoQuery {
        public static final String CMD_NAME = "get-template";
    }

    public static class Status extends TableNoQuery {
        public static final String CMD_NAME = "status";
    }
    
    public static class Set extends TableNoQuery {
        public static final String CMD_NAME = "set";
   }


    public static class Update extends TableNoQuery {
        public static final String CMD_NAME = "update";
    }


    public static class Enable extends TableNoQuery {
        public static final String CMD_NAME = "enable";
    }


    public static class Disable extends TableNoQuery {
        public static final String CMD_NAME = "disable";
    }


    public static class Start extends TableNoQuery {
        public static final String CMD_NAME = "start";
    }


    public static class Pause extends TableNoQuery {
        public static final String CMD_NAME = "pause";
    }


    public static class Resume extends TableNoQuery {
        public static final String CMD_NAME = "resume";
    }


    public static class Cancel extends TableNoQuery {
        public static final String CMD_NAME = "cancel";
    }


    public static class Upload extends TableNoQuery {
        public static final String CMD_NAME = "upload";
    }


    public static class Download extends TableNoQuery {
        public static final String CMD_NAME = "download";
    }
    
    public static class DownloadTemplate extends TableNoQuery {
        public static final String CMD_NAME = "download-template";
    }


    public static class Install extends TableNoQuery {
        public static final String CMD_NAME = "install";
    }


    public static class Uninstall extends TableNoQuery {
        public static final String CMD_NAME = "uninstall";
    }


    public static class Import extends TableNoQuery {
        public static final String CMD_NAME = "import";
    }


    public static class Export extends TableNoQuery {
        public static final String CMD_NAME = "export";
    }


    public static class Setup extends TableNoQuery {
        public static final String CMD_NAME = "setup";
    }

    public static class WaitFor extends TableNoQuery {
        public static final String CMD_NAME = "wait-for";
    }

    public static class Login extends TableNoQuery {
        public static final String CMD_NAME = "login";
    }


    public static class Logout extends TableNoQuery {
        public static final String CMD_NAME = "logout";
    }

    public static class RestCall extends DetailsWithQuery {
        public static final String CMD_NAME = "call";
    }

}
