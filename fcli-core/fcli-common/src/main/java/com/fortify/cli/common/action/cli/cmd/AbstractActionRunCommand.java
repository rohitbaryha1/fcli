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
package com.fortify.cli.common.action.cli.cmd;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.expression.spel.support.SimpleEvaluationContext;

import com.fortify.cli.common.action.cli.mixin.ActionSourceResolverMixin;
import com.fortify.cli.common.action.helper.ActionHelper;
import com.fortify.cli.common.action.runner.ActionParameterHelper;
import com.fortify.cli.common.action.runner.ActionRunner;
import com.fortify.cli.common.cli.cmd.AbstractRunnableCommand;
import com.fortify.cli.common.cli.mixin.CommandHelperMixin;
import com.fortify.cli.common.cli.mixin.CommonOptionMixins;
import com.fortify.cli.common.cli.util.SimpleOptionsParser.OptionsParseResult;
import com.fortify.cli.common.crypto.SignatureHelper.SignedTextDescriptor;
import com.fortify.cli.common.progress.cli.mixin.ProgressWriterFactoryMixin;
import com.fortify.cli.common.progress.helper.IProgressWriterI18n;
import com.fortify.cli.common.util.DisableTest;
import com.fortify.cli.common.util.DisableTest.TestType;

import lombok.SneakyThrows;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Unmatched;

public abstract class AbstractActionRunCommand extends AbstractRunnableCommand {
    @Parameters(arity="1", descriptionKey="fcli.action.run.action") private String action;
    @DisableTest({TestType.MULTI_OPT_SPLIT, TestType.MULTI_OPT_PLURAL_NAME, TestType.OPT_LONG_NAME})
    @Option(names="--<action-parameter>", paramLabel="<value>", descriptionKey="fcli.action.run.action-parameter") 
    private List<String> dummyForSynopsis;
    @Mixin private ProgressWriterFactoryMixin progressWriterFactory;
    @Mixin CommandHelperMixin commandHelper;
    @Mixin CommonOptionMixins.RequireConfirmation confirm;
    @Mixin private ActionSourceResolverMixin.OptionalOption actionSourceResolver;
    @Unmatched private String[] actionArgs;
    
    @Override @SneakyThrows
    public final Integer call() {
        initMixins();
        var loadedAction = ActionHelper.loadAction(actionSourceResolver.getActionSources(getType()), action, this::confirmInvalidSignature);
        Callable<Integer> delayedConsoleWriter = null;
        try ( var progressWriter = progressWriterFactory.create() ) {
            try ( var actionRunner = ActionRunner.builder()
                .onValidationErrors(this::onValidationErrors)
                .action(loadedAction)
                .progressWriter(progressWriter)
                .rootCommandLine(commandHelper.getRootCommandLine())
                .build() ) 
            {
                delayedConsoleWriter = run(actionRunner, progressWriter);
            }
        }
        return delayedConsoleWriter.call();
    }
    
    private void confirmInvalidSignature(SignedTextDescriptor descriptor) {
        confirm.checkConfirmed("WARN: "+ActionHelper.getSignatureStatusMessage(descriptor.getSignatureStatus()));
    }

    private Callable<Integer> run(ActionRunner actionRunner, IProgressWriterI18n progressWriter) {
        actionRunner.getSpelEvaluator().configure(context->configure(actionRunner, context));
        progressWriter.writeProgress("Executing action %s", actionRunner.getAction().getName());
        return actionRunner.run(actionArgs);
    }
    
    private ParameterException onValidationErrors(OptionsParseResult optionsParseResult) {
        var errorsString = String.join("\n ", optionsParseResult.getValidationErrors());
        var supportedOptionsString = ActionParameterHelper.getSupportedOptionsTable(optionsParseResult.getOptions());
        var msg = String.format("Option errors:\n %s\nSupported options:\n%s\n", errorsString, supportedOptionsString);
        return new ParameterException(commandHelper.getCommandSpec().commandLine(), msg);
    }

    protected abstract String getType();
    protected abstract void configure(ActionRunner actionRunner, SimpleEvaluationContext context);
}
