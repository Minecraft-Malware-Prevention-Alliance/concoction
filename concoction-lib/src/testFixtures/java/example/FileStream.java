package example;

import java.io.*;

public class FileStream {
	public static String streamImportantFile() throws IOException {
		FileInputStream fis = new FileInputStream("important.txt");
		try (BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
			return sb.toString();
		}
	}
}
