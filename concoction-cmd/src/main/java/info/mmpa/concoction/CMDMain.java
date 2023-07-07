package info.mmpa.concoction;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.Callable;

import info.mmpa.concoction.result.ArchiveScanResults;
import info.mmpa.concoction.result.printer.HumanReadablePrinter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "Concoction", mixinStandardHelpOptions = true, version = "Concoction v1.0.0", description = "Dynamic Shared Malware Scanner")
public class CMDMain implements Callable<Integer> {
    @Parameters(index = "0", description = "The file / directory to scan")
    private File file;

    @Override
    public Integer call() throws Exception {
        Map<String, ArchiveScanResults> results = Concoction.scanDirectory(file);

        if (results.isEmpty()) {
            return CommandLine.ExitCode.OK;
        } else {
            new HumanReadablePrinter(results).print(new PrintWriter(System.out));
            return 1;
        }
    }

    public static void main(String[] args) {
        final int exitCode = new CommandLine(new CMDMain()).execute(args);
        System.exit(exitCode);
    }
}
