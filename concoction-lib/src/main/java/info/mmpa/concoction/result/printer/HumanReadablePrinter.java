package info.mmpa.concoction.result.printer;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;

import info.mmpa.concoction.result.ArchiveScanResults;
import info.mmpa.concoction.result.CheckResults;
import info.mmpa.concoction.result.FileScanResults;
import info.mmpa.concoction.result.TestResultLevel;

public class HumanReadablePrinter extends Printer {

    public HumanReadablePrinter(Map<String, ArchiveScanResults> scanResults) {
        super(scanResults);
    }

    public HumanReadablePrinter(String archiveName, ArchiveScanResults scanResult) {
        super(archiveName, scanResult);
    }

    private void print(PrintWriter writer, String archiveName, ArchiveScanResults archiveResults, String formatBase, boolean first) {
        writer.printf(formatBase, (first ? "Results for archive " : "Results for bundled archive ") + archiveName);
        formatBase = "  " + formatBase;

        if (archiveResults.errorReadingArchive) {
            writer.printf(formatBase, "Error while reading archive " + archiveName + ", scan results may be missing");
        }

        // TODO Better formatting code
        final String indentOne = "  " + formatBase;
        final String indentTwo = "  " + indentOne;
        final String indentThree = "  " + indentTwo;
        final String indentFour = "  " + indentThree;
        archiveResults.recursiveArchiveScanResults.forEach((e, f) -> print(writer, e, f, indentOne, false));
        boolean firstLog = true;

        for (final Entry<String, FileScanResults> fileResultGroup : archiveResults.fileScanResults.entrySet()) {
            boolean firstLogForFile = true;

            for (final Entry<TestResultLevel, Map<String, CheckResults>> scanGroup : fileResultGroup.getValue().scanResultGroups.entrySet()) {
                final TestResultLevel groupLevel = scanGroup.getKey();

                if (firstLog) {
                    writer.printf(formatBase, "Detected results for archive " + archiveName);
                    firstLog = false;
                }

                if (firstLogForFile) {
                    writer.printf(indentOne, "Detected results for file " + fileResultGroup.getKey());
                    firstLogForFile = false;
                }

                writer.printf(indentTwo, "Result level " + groupLevel);
                final Map<String, CheckResults> resultMap = scanGroup.getValue();

                for (final Entry<String, CheckResults> checkEntry : resultMap.entrySet()) {
                    writer.printf(indentThree, "Result found by checker " + checkEntry.getKey());

                    for (final Entry<String, Integer> scanEntry : checkEntry.getValue().checkResultsMap.entrySet()) {
                        writer.printf(indentFour, "Detected " + scanEntry.getValue() + " time(s): " + scanEntry.getKey());
                    }
                }
            }
        }
    }

    @Override
    public void print(PrintWriter writer) {
        scanResults.forEach((archiveName, archiveResults) -> print(writer, archiveName, archiveResults, "- %s%n", true));
    }

}
