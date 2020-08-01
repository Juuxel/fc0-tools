package juuxel.fc0.tools.test;

import juuxel.fc0.tools.source.EnumFixer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnumFixerTests {
	@Test
	@DisplayName("Basic enum without values() or interfaces")
	void basic() throws IOException {
		List<String> input = TestUtil.readLines("BasicInput.txt");
		List<String> expected = TestUtil.readLines("BasicOutput.txt");

		List<String> result = EnumFixer.fixEnums(input);

		assertNotNull(result);
		assertEquals(expected, result);
	}

	@Test
	@DisplayName("Complex enum with values() and interfaces")
	void complex() throws IOException {
		List<String> input = TestUtil.readLines("ComplexInput.txt");
		List<String> expected = TestUtil.readLines("ComplexOutput.txt");

		List<String> result = EnumFixer.fixEnums(input);

		assertNotNull(result);
		assertEquals(expected, result);
	}

	@Test
	@DisplayName("A regular class that is not an enum")
	void notAnEnum() throws IOException {
		List<String> input = TestUtil.readLines("NotAnEnum.txt");
		List<String> result = EnumFixer.fixEnums(input);

		assertNull(result);
	}
}
