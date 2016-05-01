package me.cybermaxke.spongeliteclient;

import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.PluginChannelListener;
import com.mumfrey.liteloader.ShutdownListener;
import me.cybermaxke.spongeliteclient.keyboard.KeyBindingStorage;
import me.cybermaxke.spongeliteclient.keyboard.KeyboardNetworkHandler;
import me.cybermaxke.spongeliteclient.network.MessageChannelHandler;
import me.cybermaxke.spongeliteclient.network.MessageRegistry;
import me.cybermaxke.spongeliteclient.network.types.MessageKeyState;
import me.cybermaxke.spongeliteclient.network.types.MessageKeyboardData;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LiteModSpongeClient implements LiteMod, ShutdownListener, PluginChannelListener {

    public static final String NAME = "SpongeLiteClient";
    public static final String VERSION = "1.0.0";

    private static LiteModSpongeClient instance;

    public static LiteModSpongeClient getInstance() {
        return instance;
    }

    /**
     * The logger of this mod.
     */
    private Logger logger;

    /**
     * The handler which will process messages send between
     * client and server.
     */
    private MessageChannelHandler channelHandler;

    /**
     * The storage (save file) for custom key bindings.
     */
    private KeyBindingStorage keyBindingStorage;

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void init(File configPath) {
        instance = this;

        this.logger = LogManager.getLogger(LiteModSpongeClient.class);

        MessageRegistry messageRegistry = new MessageRegistry();
        messageRegistry.register(2, MessageKeyboardData.class, KeyboardNetworkHandler::handleKeyboardData);
        messageRegistry.register(3, MessageKeyState.class);

        this.channelHandler = new MessageChannelHandler(messageRegistry, "Sponge", this.logger);

        this.keyBindingStorage = new KeyBindingStorage(new File("").toPath().resolve("sponge-key-bindings.txt"));
        try {
            this.keyBindingStorage.load();
        } catch (IOException e) {
            this.logger.error("An error occurred while loading the key bindings file", e);
        }
    }

    @Override
    public void onShutDown() {
        try {
            this.keyBindingStorage.save();
        } catch (IOException e) {
            this.logger.error("An error occurred while saving the key bindings file", e);
        }
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {
    }

    @Override
    public String getName() {
        return NAME;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public MessageChannelHandler getChannelHandler() {
        return this.channelHandler;
    }

    public KeyBindingStorage getKeyBindingStorage() {
        return this.keyBindingStorage;
    }

    @Override
    public void onCustomPayload(String channel, PacketBuffer data) {
        this.channelHandler.onCustomPayload(channel, data);
    }

    @Override
    public List<String> getChannels() {
        return this.channelHandler.getChannels();
    }
}
