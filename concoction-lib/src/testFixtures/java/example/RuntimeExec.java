package example;

import java.io.IOException;

public class RuntimeExec {
	public static void calc() throws IOException {
		Runtime.getRuntime().exec("calc");
	}
}
