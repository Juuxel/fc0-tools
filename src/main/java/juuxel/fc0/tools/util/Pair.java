/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.fc0.tools.util;

import java.util.Objects;

/**
 * A pair of two values.
 *
 * @param <A> The type of the first value.
 * @param <B> The type of the second value.
 */
public final class Pair<A, B> {
	private final A first;
	private final B second;

	/**
	 * Constructs a pair.
	 *
	 * @param first  The first value.
	 * @param second The second value.
	 */
	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Gets the first value of this pair.
	 *
	 * @return The first value.
	 */
	public A getFirst() {
		return first;
	}

	/**
	 * Gets the second value of this pair.
	 *
	 * @return The second value.
	 */
	public B getSecond() {
		return second;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Pair<?, ?> pair = (Pair<?, ?>) o;
		return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}

	@Override
	public String toString() {
		return "Pair[" + first + ", " + second + "]";
	}
}
