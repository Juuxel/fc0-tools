/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.fc0.tools.source;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class EnumFixer {
	private static final Pattern CLASS_REGEX = Pattern.compile("(?:(.+) )?final class (.+)");
	private static final Pattern EXTENDS_REGEX = Pattern.compile("extends Enum<(\\w+)>(?: \\{)?");
	private static final Pattern ENUM_FIELD_REGEX = Pattern.compile(" {4}public static final /\\* enum \\*/ \\w+ (\\w+) = new \\w+(\\(.*\\));");
	private static final String CLOSE_METHOD = "    }";

	private static String getValuesRegex(String enumName) {
		return " {4}public static " + enumName + "\\[\\] values\\(\\) \\{";
	}

	/**
	 * Fixes top-level enum declarations in a Java source file decompiled by CFR.
	 *
	 * <p>The input of this method needs to be in this exact format:
	 *
	 * <pre>{@code
	 * // imports
	 *
	 * public final class Whatever
	 * extends Enum<Whatever>
	 * implements SomeInterfaces {
	 *     public static final ENUM_COMMENT Whatever FIELD_NAME = new Whatever(...);
	 * }</pre>
	 *
	 * where {@code ENUM_COMMENT} is an inline comment with {@code ' enum '} as its content.
	 *
	 * @param lines The source code lines.
	 * @return The fixed source code lines, or null if there's no top-level enum.
	 */
	@Nullable
	public static List<String> fixEnums(List<String> lines) {
		// TODO: Nested enum support; 2fc doesn't use them but it doesn't hurt to support them

		int index = -1; // The index of the 'extends Enum' line
		String enumName = null;

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);

			Matcher extendsMatcher = EXTENDS_REGEX.matcher(line);
			if (extendsMatcher.matches()) {
				enumName = extendsMatcher.group(1);
				index = i;
			}
		}

		if (index == -1) return null; // Nothing to process

		List<String> target = new ArrayList<>();
		boolean wasField = false;
		boolean inValues = false;

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);

			if (inValues) {
				if (line.equals(CLOSE_METHOD)) {
					inValues = false;
				}
				continue;
			}

			if (i >= index - 1) {
				Matcher classMatcher = CLASS_REGEX.matcher(line);

				if (classMatcher.matches()) {
					String access = classMatcher.group(1);
					if (access == null) {
						access = "";
					} else {
						access += " ";
					}
					target.add(access + "enum " + classMatcher.group(2));
					continue;
				} else if (EXTENDS_REGEX.matcher(line).matches()) {
					if (line.endsWith("{")) {
						String prev = target.get(target.size() - 1);
						target.set(target.size() - 1, prev + " {");
					}

					continue;
				}

				Matcher enumMatcher = ENUM_FIELD_REGEX.matcher(line);

				if (enumMatcher.matches()) {
					String name = enumMatcher.group(1);
					String args = enumMatcher.group(2);

					// Cosmetic: "()" -> ""
					if ("()".equals(args)) {
						args = "";
					}

					target.add("    " + name + args + ",");

					wasField = true;
					continue;
				}

				// Yeet the values() method
				if (line.matches(getValuesRegex(enumName))) {
					inValues = true;
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
