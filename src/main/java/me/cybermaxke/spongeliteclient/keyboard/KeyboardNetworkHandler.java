package me.cybermaxke.spongeliteclient.keyboard;

import com.mumfrey.liteloader.core.LiteLoader;
import me.cybermaxke.spongeliteclient.LiteModSpongeClient;
import me.cybermaxke.spongeliteclient.interfaces.IMixinKeyBinding;
import me.cybermaxke.spongeliteclient.network.types.MessageKeyboardData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KeyboardNetworkHandler {

    private static boolean initialized;

    public static void handleKeyboardData(INetHandlerPlayClient handler, MessageKeyboardData message) {
        if (initialized) {
            return;
        }
        // Inject the default internal ids
        GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
        ((IMixinKeyBinding) gameSettings.keyBindAttack).setInternalId(0);
        ((IMixinKeyBinding) gameSettings.keyBindPickBlock).setInternalId(1);
        ((IMixinKeyBinding) gameSettings.keyBindUseItem).setInternalId(2);
        ((IMixinKeyBinding) gameSettings.keyBindDrop).setInternalId(3);
        for (int i = 0; i < 9; i++) {
            ((IMixinKeyBinding) gameSettings.keyBindsHotbar[i]).setInternalId(4 + i);
        }
        ((IMixinKeyBinding) gameSettings.keyBindInventory).setInternalId(13);
        ((IMixinKeyBinding) gameSettings.keyBindSwapHands).setInternalId(14);
        ((IMixinKeyBinding) gameSettings.keyBindFullscreen).setInternalId(15);
        ((IMixinKeyBinding) gameSettings.keyBindScreenshot).setInternalId(16);
        ((IMixinKeyBinding) gameSettings.keyBindSmoothCamera).setInternalId(17);
        ((IMixinKeyBinding) gameSettings.keyBindSpectatorOutlines).setInternalId(18);
        ((IMixinKeyBinding) gameSettings.keyBindTogglePerspective).setInternalId(19);
        ((IMixinKeyBinding) gameSettings.keyBindBack).setInternalId(20);
        ((IMixinKeyBinding) gameSettings.keyBindForward).setInternalId(21);
        ((IMixinKeyBinding) gameSettings.keyBindJump).setInternalId(22);
        ((IMixinKeyBinding) gameSettings.keyBindLeft).setInternalId(23);
        ((IMixinKeyBinding) gameSettings.keyBindRight).setInternalId(24);
        ((IMixinKeyBinding) gameSettings.keyBindSneak).setInternalId(25);
        ((IMixinKeyBinding) gameSettings.keyBindSprint).setInternalId(26);
        ((IMixinKeyBinding) gameSettings.keyBindChat).setInternalId(27);
        ((IMixinKeyBinding) gameSettings.keyBindCommand).setInternalId(28);
        ((IMixinKeyBinding) gameSettings.keyBindPlayerList).setInternalId(29);

        // Get the key binding storage
        KeyBindingStorage storage = LiteModSpongeClient.getInstance().getKeyBindingStorage();

        // Build all the custom key bindings
        // List<net.minecraft.client.settings.KeyBinding> keyBindings = new ArrayList<>(Arrays.asList(gameSettings.keyBindings)); // SpongeForge
        for (KeyBinding keyBinding : message.getKeyBindings()) {
            CustomClientKeyBinding clientKeyBinding = new CustomClientKeyBinding(keyBinding);
            storage.getKeyCode(keyBinding.getId().toLowerCase()).ifPresent(clientKeyBinding::setKeyCode);
            // keyBindings.add(clientKeyBinding);
            LiteLoader.getInput().registerKeyBinding(clientKeyBinding);
        }
        // Update the key bindings array in the settings menu
        // gameSettings.keyBindings = keyBindings.toArray(new net.minecraft.client.settings.KeyBinding[keyBindings.size()]); // SpongeForge
        initialized = true;
    }

    public static void handleCleanup() {
        if (!initialized) {
            return;
        }
        // GameSettings gameSettings = Minecraft.getMinecraft().gameSettings; // SpongeForge
        // List<net.minecraft.client.settings.KeyBinding> keyBindings = new ArrayList<>(Arrays.asList(gameSettings.keyBindings)); // SpongeForge
        List<net.minecraft.client.settings.KeyBinding> keyBindings = new ArrayList<>(LiteLoader.getGameEngine().getKeyBindings()); // Liteloader
        Iterator<net.minecraft.client.settings.KeyBinding> it = keyBindings.iterator();
        // Get the key binding storage
        KeyBindingStorage storage = LiteModSpongeClient.getInstance().getKeyBindingStorage();
        while (it.hasNext()) {
            net.minecraft.client.settings.KeyBinding keyBinding = it.next();
            if (keyBinding instanceof CustomClientKeyBinding) {
                // ((IMixinKeyBinding) keyBinding).remove(); // SpongeForge
                storage.putKeyCode(((CustomClientKeyBinding) keyBinding).getId(), keyBinding.getKeyCode());
                // it.remove(); // SpongeForge
                LiteLoader.getInput().unRegisterKeyBinding(keyBinding); // Liteloader
            } else {
                ((IMixinKeyBinding) keyBinding).setInternalId(-1);
            }
        }
        try {
            storage.save();
        } catch (IOException e) {
            LiteModSpongeClient.getInstance().getLogger().error("An error occurred while saving the key bindings file", e);
        }
        // Update the key bindings array in the settings menu
        // gameSettings.keyBindings = keyBindings.toArray(new net.minecraft.client.settings.KeyBinding[keyBindings.size()]); // SpongeForge
        // Reprocess the key bindings mappings after removal
        // net.minecraft.client.settings.KeyBinding.resetKeyBindingArrayAndHash(); // SpongeForge
        initialized = false;
    }
}
