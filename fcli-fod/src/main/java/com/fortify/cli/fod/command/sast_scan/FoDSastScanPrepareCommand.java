/*******************************************************************************
 * (c) Copyright 2021 Micro Focus or one of its affiliates
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
package com.fortify.cli.fod.command.sast_scan;

import com.fortify.cli.common.picocli.annotation.SubcommandOf;
import com.fortify.cli.common.sast.picocli.command.sast_scan.prepare.SastCleanCommand;
import com.fortify.cli.common.sast.picocli.command.sast_scan.prepare.SastPackageCommand;
import com.fortify.cli.common.sast.picocli.command.sast_scan.prepare.SastTranslateCommand;

import io.micronaut.core.annotation.Order;
import io.micronaut.core.annotation.ReflectiveAccess;
import picocli.CommandLine.Command;

@ReflectiveAccess
@SubcommandOf(FoDSastScanCommand.class)
@Command(name = "prepare", description = "Prepare for an FoD SAST scan")
@Order(FoDSastScanCommandsOrder.PREPARE)
public class FoDSastScanPrepareCommand {
	@SubcommandOf(FoDSastScanPrepareCommand.class) @Order(10)
	public static final class Clean extends SastCleanCommand {}
	
	@SubcommandOf(FoDSastScanPrepareCommand.class) @Order(20)
	public static final class Translate extends SastTranslateCommand {}
	
	@SubcommandOf(FoDSastScanPrepareCommand.class) @Order(30)
	public static final class Package extends SastPackageCommand {}
}