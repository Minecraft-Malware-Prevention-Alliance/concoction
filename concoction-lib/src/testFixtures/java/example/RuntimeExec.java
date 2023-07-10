package example;

import java.io.IOException;

public class RuntimeExec {
	private static final Runtime runtime = Runtime.getRuntime();
	private static final Object runtimeObject = runtime;

	public static void calc1() throws IOException {
		Runtime.getRuntime().exec("calc");
	}

	public static void calc2() throws IOException {
		Runtime runtime = Runtime.getRuntime();
		runtime.exec("calc");
	}

	public static void calc3() throws IOException {
		runtime.exec("calc");
	}

	public static void calc4() throws IOException {
		((Runtime) runtimeObject).exec("calc");
	}
}
