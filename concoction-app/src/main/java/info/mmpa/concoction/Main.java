package info.mmpa.concoction;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "Concoction", mixinStandardHelpOptions = true,
		version = "Concoction v" + ConcoctionBuildConfig.VERSION,
		description = "Dynamic Shared Malware Scanner")
public class Main implements Callable<Integer> {

	@Override
	public Integer call() throws Exception {
        // TODO: Reimplement once the core library API solidifies
		return 0;
	}

	public static void main(String[] args) {
		final int exitCode = new CommandLine(new Main()).execute(args);
		System.exit(exitCode);
	}
}
