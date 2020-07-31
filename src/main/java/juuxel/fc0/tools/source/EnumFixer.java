package juuxel.fc0.tools.source;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class EnumFixer {
	private static final String CLASS_PREFIX = "public final class ";
	private static final Pattern ENUM_FIELD_REGEX = Pattern.compile(" {4}public static final /* enum */ \\w+ (\\w+) = new \\w+(\\(.+\\));");

	public static List<String> fixEnums(List<String> lines) {
		// TODO: Nested enum support; 2fc doesn't use them but it doesn't hurt to support them

		int index = -1; // The index of the 'extends Enum' line

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);

			if (line.startsWith("extends Enum<")) {
				index = i;
			}
		}

		if (index == -1) return lines; // Nothing to process

		List<String> target = new ArrayList<>();
		boolean wasField = false;

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);

			if (i >= index - 1) {
				if (line.startsWith(CLASS_PREFIX)) {
					target.add("public enum " + line.substring(CLASS_PREFIX.length()));
					continue;
				}

				Matcher enumMatcher = ENUM_FIELD_REGEX.matcher(line);

				if (enumMatcher.matches()) {
					target.add("    " + enumMatcher.group(1) + enumMatcher.group(2) + ",");

					wasField = true;
					continue;
				}
			}

			if (wasField) {
				target.add("    ;");
				wasField = false;
			}

			target.add(line);
		}

		return target;
	}
}
