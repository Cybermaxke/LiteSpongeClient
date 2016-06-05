/*
 * This file is part of LiteSpongeClient, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.client.keyboard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class KeyBindingStorage {

    private final Map<String, Integer> keyBindings = new HashMap<>();
    private final Path path;

    public KeyBindingStorage(Path path) {
        this.path = path;
    }

    /**
     * Gets the key code for the specified identifier, may be absent.
     *
     * @param identifier The identifier
     * @return The key code
     */
    public Optional<Integer> get(String identifier) {
        return Optional.ofNullable(this.keyBindings.get(identifier));
    }

    /**
     * Puts a key code in the storage for the specified identifier.
     *
     * @param identifier The identifier
     * @param keyCode The key code
     */
    public void put(String identifier, int keyCode) {
        this.keyBindings.put(identifier, keyCode);
    }

    public void save() throws IOException {
        if (Files.isDirectory(this.path)) {
            throw new IOException("Path must be a file!");
        }

        Properties properties = new Properties();
        for (Map.Entry<String, Integer> entry : this.keyBindings.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            properties.put(key, value);
        }

        properties.store(Files.newOutputStream(this.path), "The sponge (plugin) key binding mappings");
    }

    public void load() throws IOException {
        if (Files.isDirectory(this.path)) {
            throw new IOException("Path must be a file!");
        }
        this.keyBindings.clear();
        if (!Files.exists(this.path)) {
            return;
        }

        Properties properties = new Properties();
        properties.load(Files.newInputStream(this.path));

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            int index = value.indexOf(';');
            if (index != -1) {
                String keyModifier = value.substring(index + 1).toUpperCase();
                // Key modifiers are not supported
                if (!keyModifier.equalsIgnoreCase("none")) {
                    continue;
                }
                value = value.substring(0, index);
            }

            int keyCode = Integer.parseInt(value);
            this.put(key, keyCode);
        }
    }
}
