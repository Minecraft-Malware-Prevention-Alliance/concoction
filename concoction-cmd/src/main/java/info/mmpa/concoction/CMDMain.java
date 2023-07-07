package info.mmpa.concoction;

import java.io.File;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "Concoction", mixinStandardHelpOptions = true, version = "Concoction v1.0.0", description = "Dynamic Shared Malware Scanner")
public class CMDMain implements Callable<Integer> {
    @Parameters(index = "0", description = "The file / directory to scan")
    private File file;

    @Override
    public Integer call() throws Exception {
        return Concoction.scanDirectory(file) ? 1 : CommandLine.ExitCode.OK;
    }

    public static void main(String[] args) {
        final int exitCode = new CommandLine(new CMDMain()).execute(args);
        System.exit(exitCode);
    }
}
