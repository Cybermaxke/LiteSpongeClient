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

import org.spongepowered.client.LiteModSpongeClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

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
    public Optional<Integer> getKeyCode(String identifier) {
        return Optional.ofNullable(this.keyBindings.get(identifier));
    }

    /**
     * Puts a key code in the storage for the specified identifier.
     *
     * @param identifier The identifier
     * @param keyCode The key code
     */
    public void putKeyCode(String identifier, int keyCode) {
        this.keyBindings.put(identifier, keyCode);
    }

    public void save() throws IOException {
        this.save(this.path);
    }

    public void save(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            throw new IOException("Path must be a file!");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Map.Entry<String, Integer> entry : this.keyBindings.entrySet()) {
                writer.write(entry.getKey() + '=' + entry.getValue());
            }
        }
    }

    public void load() throws IOException {
        this.load(this.path);
    }

    private void load(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            throw new IOException("Path must be a file!");
        }
        this.keyBindings.clear();
        if (!Files.exists(path)) {
            return;
        }

        int lineIndex = 0;
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            Iterator<String> it = reader.lines().iterator();
            while (it.hasNext()) {
                lineIndex++;
                String line = it.next();

                int index = line.lastIndexOf('=');
                if (index == -1) {
                    LiteModSpongeClient.getInstance().getLogger().warn("Invalid key binding mapping in file {} at line index {}, skipping...",
                            path.toString(), lineIndex);
                    continue;
                }

                String id = line.substring(0, index);
                if (id.isEmpty()) {
                    LiteModSpongeClient.getInstance().getLogger().warn("Invalid key binding mapping in file {} at line index {} (id is empty), "
                            + "skipping...", path.toString(), lineIndex);
                    continue;
                }

                String sKeyCode = line.substring(index + 1);
                if (sKeyCode.isEmpty()) {
                    LiteModSpongeClient.getInstance().getLogger().warn("Invalid key binding mapping in file {} at line index {} (keyCode is empty), "
                            + "skipping...", path.toString(), lineIndex);
                    continue;
                }

                int keyCode;
                try {
                    keyCode = Integer.parseInt(sKeyCode);
                } catch (NumberFormatException e) {
                    LiteModSpongeClient.getInstance().getLogger().warn("Invalid key binding mapping in file {} at line index {} (keyCode is "
                            + "invalid), skipping...", path.toString(), lineIndex);
                    continue;
                }

                this.keyBindings.put(id, keyCode);
            }
        }
    }
}
