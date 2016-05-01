package me.cybermaxke.spongeliteclient.keyboard;

import me.cybermaxke.spongeliteclient.LiteModSpongeClient;

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
        Files.createDirectories(path.getParent());

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
