package juuxel.fc0.tools.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

final class TestUtil {
	static List<String> readLines(String file) throws IOException {
		return readLines(TestUtil.class.getResourceAsStream("/" + file));
	}

	static List<String> readLines(InputStream in) throws IOException {
		List<String> result = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			String line;

			while ((line = reader.readLine()) != null) {
				result.add(line);
			}
		}

		return result;
	}
}
