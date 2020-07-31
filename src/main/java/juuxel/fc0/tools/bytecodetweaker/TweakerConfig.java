/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package juuxel.fc0.tools.bytecodetweaker;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import juuxel.fc0.tools.util.Pair;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class TweakerConfig {
	final Map<String, Set<String>> bridges;

	public TweakerConfig(Map<String, Set<String>> bridges) {
		this.bridges = bridges;
	}

	private static String asString(JsonElement json) {
		return ((JsonPrimitive) json).asString();
	}

	public static TweakerConfig fromJson(JsonObject json) {
		Map<String, Set<String>> bridges = Collections.emptyMap();

		if (json.containsKey("bridges")) {
			bridges = json.getObject("bridges").entrySet().stream()
				.map(entry -> new Pair<>(
					entry.getKey(),
					((JsonArray) entry.getValue()).stream().map(TweakerConfig::asString).collect(Collectors.toSet()))
				)
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
		}

		return new TweakerConfig(bridges);
	}
}
