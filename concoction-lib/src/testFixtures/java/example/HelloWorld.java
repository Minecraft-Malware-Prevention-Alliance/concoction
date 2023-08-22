package example;

public class HelloWorld {
	public static void start() {
		print(getMessage());
	}

	private static void print(String message) {
		System.out.println(message);
	}

	private static String getMessage() {
		return "Hello World";
	}
}
