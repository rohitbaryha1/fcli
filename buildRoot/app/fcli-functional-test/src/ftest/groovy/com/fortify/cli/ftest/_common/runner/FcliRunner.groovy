package com.fortify.cli.ftest._common.runner;

import java.nio.file.Files
import java.nio.file.Path

import com.fortify.cli.ftest._common.Input

import groovy.transform.CompileStatic
import groovy.transform.Immutable

@CompileStatic
public class FcliRunner {
    private static Path fcliDataDir;
    private static IRunner runner
    
    static void initialize() {
        System.setProperty("picocli.ansi", "false")
        fcliDataDir = Files.createTempDirectory("fcli").toAbsolutePath()
        System.setProperty("fcli.env.FORTIFY_DATA_DIR", fcliDataDir.toString())
        println("Using fcli data directory "+fcliDataDir)
        runner = createRunner()
    }
    
    static boolean run(String[] args) {
        if ( !runner ) {
            throw new IllegalStateException("Runner not initialized")
        } 
        try {
            return runner.run(args)
        } catch ( Exception e ) {
            e.printStackTrace()
            return false
        }
    }
    
    static void close() {
        if ( runner ) { 
            runner.close()
        }
        try {
            Files.walk(fcliDataDir)
                .sorted(Comparator.reverseOrder())
                .map(Path.&toFile)
                .forEach(File.&delete); // For some reason this throws an exception on the
                                        // top-level directory, but the full directory tree
                                        // is being deleted anyway. As such, we just swallow
                                        // any exceptions, and print an error if the directory 
                                        // still exists afterwards. 
        } catch ( IOException e ) {}
        if ( fcliDataDir.toFile().exists() ) {
            println "Error deleting directory "+fcliDataDir+", please clean up manually";
        }
    }
    
    private static IRunner createRunner() {
        String fcli = Input.FcliCommand.get()
        String java = Input.JavaCommand.get() ?: "java"
        if ( !fcli || fcli=="build" ) {
            return new ReflectiveRunner()
        } else {
            def cmd = fcli.endsWith(".jar")
                ? [java, "-jar", fcli]
                : [fcli]
            return new ExternalRunner(cmd)
        }
    }
    
    private static interface IRunner extends AutoCloseable {
        boolean run(String... args);
    }
    
    @Immutable
    private static class ExternalRunner implements IRunner {
        List fcliCmd
        
        @Override
        boolean run(String... args) {
            def argsList = args as List
            def fullCmd = fcliCmd+argsList
            def proc = fullCmd.execute()
            proc.consumeProcessOutput(System.out, System.err)
            proc.waitForOrKill(60000)
            proc.exitValue()==0
        }
        
        @Override
        void close() {}
    }
    
    // We need to use reflection for instantiating/invoking DefaultFortifyCLIRunner,
    // as this class (and IFortifyCLIRunner interface) is not available if the
    // ftest.fcli property points to an external fcli executable
    private static class ReflectiveRunner implements IRunner {
        private Object obj;
        
        @Override
        boolean run(String... args) {
            if ( obj==null ) {
                obj = Class.forName("com.fortify.cli.app.runner.DefaultFortifyCLIRunner").newInstance()
            }
            0==(int)obj.invokeMethod("run", args)
        }
        
        @Override
        void close() {
            if ( obj!=null ) {
                obj.invokeMethod("close", [])
            }
        }
    }
}