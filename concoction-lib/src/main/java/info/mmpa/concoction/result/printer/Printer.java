package info.mmpa.concoction.result.printer;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import info.mmpa.concoction.result.ArchiveScanResults;

public abstract class Printer {
    protected final Map<String, ArchiveScanResults> scanResults;

    protected Printer(Map<String, ArchiveScanResults> scanResults) {
        this.scanResults = scanResults;
    }

    protected Printer(String archiveName, ArchiveScanResults scanResult) {
        this(new HashMap<>());
        scanResults.put(archiveName, scanResult);
    }

    public abstract void print(PrintWriter writer);
}
