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
	private static final String CLASS_PREFIX = "public final class ";
	private static final String EXTENDS_PREFIX = "extends Enum<";
	private static final Pattern ENUM_FIELD_REGEX = Pattern.compile(" {4}public static final /\\* enum \\*/ \\w+ (\\w+) = new \\w+(\\(.*\\));");

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

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);

			if (line.startsWith("extends Enum<")) {
				index = i;
			}
		}

		if (index == -1) return null; // Nothing to process

		List<String> target = new ArrayList<>();
		boolean wasField = false;

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);

			if (i >= index - 1) {
				if (line.startsWith(CLASS_PREFIX)) {
					target.add("public enum " + line.substring(CLASS_PREFIX.length()));
					continue;
				} else if (line.startsWith(EXTENDS_PREFIX)) {
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
