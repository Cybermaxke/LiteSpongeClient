package me.cybermaxke.spongeliteclient.mixin.client.settings;

import com.mumfrey.liteloader.core.LiteLoader;
import me.cybermaxke.spongeliteclient.LiteModSpongeClient;
import me.cybermaxke.spongeliteclient.interfaces.IMixinKeyBinding;
import me.cybermaxke.spongeliteclient.keyboard.IClientKeyBinding;
import me.cybermaxke.spongeliteclient.network.types.MessageKeyState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.IntHashMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(KeyBinding.class)
public abstract class MixinKeyBinding implements IMixinKeyBinding, Comparable<KeyBinding> {

    // @Shadow private static List<KeyBinding> KEYBIND_ARRAY; // SpongeForge
    @Shadow private static IntHashMap<KeyBinding> HASH;
    @Shadow private boolean pressed;
    @Shadow private int pressTime;
    @Final @Shadow private String keyCategory;
    @Final @Shadow private String keyDescription;

    private int internalId = -1;

    @Override
    public void setInternalId(int internalId) {
        this.internalId = internalId;
    }

    @Override
    public void setPressed(boolean pressed) {
        boolean lastState = this.pressed;
        this.pressed = pressed;
        // Check if the state has changed and if we are on
        // a server that supports the key bindings
        if (lastState != pressed && this.internalId != -1) {
            // Send a key state change event
            LiteModSpongeClient.getInstance().getChannelHandler().sendToServer(new MessageKeyState(this.internalId, pressed));
        }
    }

    @Override
    public void setPressTime(int time) {
        this.pressTime = time;
    }

    /*
    @Override
    public void remove() { // SpongeForge
        KEYBIND_ARRAY.remove(this);
    }
    */

    @Override
    public String getCategory() {
        return this.keyCategory;
    }

    @Override
    public String getFormattedCategory() {
        return I18n.format(this.keyCategory);
    }

    @Override
    public String getDisplayName() {
        return this.keyDescription;
    }

    @Override
    public String getFormattedDisplayName() {
        return I18n.format(this.keyDescription);
    }

    /**
     * Is actually getCategories
     *
     * @author Cybermaxke
     * @reason This is overwritten to use the key bindings that are in the array,
     * it is hard to keep track of categories in a set if one key binding
     * using that category gets removed.
     *
     * @return The category names
     */
    @Overwrite
    public static Set<String> getKeybinds() {
        // return KEYBIND_ARRAY.stream().map(keyBinding -> ((IMixinKeyBinding) keyBinding).getCategory()).collect(Collectors.toSet()); // SpongeForge
        // LiteLoader
        return LiteLoader.getGameEngine().getKeyBindings().stream()
                .map(keyBinding -> ((IClientKeyBinding) keyBinding).getCategory())
                .collect(Collectors.toSet());
    }

    /**
     * @author Cybermaxke
     * @reason Overwritten to delegate the pressed state change through a method,
     * which allows us to track the change and send network messages.
     *
     * @param keyCode The key code
     * @param pressed The pressed state
     */
    @Overwrite
    public static void setKeyBindState(int keyCode, boolean pressed) {
        if (keyCode != 0) {
            IMixinKeyBinding keyBinding = (IMixinKeyBinding) HASH.lookup(keyCode);
            if (keyBinding != null) {
                keyBinding.setPressed(pressed);
            }
        }
    }

    /**
     * @author Cybermaxke
     * @reason Overwritten to delegate the pressed state change through a method,
     * which allows us to track the change and send network messages.
     */
    @Overwrite
    public static void unPressAllKeys() {
        // List<KeyBinding> keyBindings = KEYBIND_ARRAY; // SpongeForge
        List<KeyBinding> keyBindings = LiteLoader.getGameEngine().getKeyBindings(); // LiteLoader
        for (KeyBinding keyBinding : keyBindings) {
            IMixinKeyBinding keyBinding0 = (IMixinKeyBinding) keyBinding;
            keyBinding0.setPressed(false);
            keyBinding0.setPressTime(0);
        }
    }

    /**
     * @author Cybermaxke
     * @reason Overwritten to properly compare the key bindings.
     *
     * @param keyBinding The other key binding
     * @return The result
     */
    @Overwrite
    @Override
    public int compareTo(KeyBinding keyBinding) {
        int i = this.getFormattedCategory().compareTo(((IClientKeyBinding) keyBinding).getFormattedCategory());
        if (i == 0) {
            i = this.getFormattedDisplayName().compareTo(((IClientKeyBinding) keyBinding).getFormattedDisplayName());
        }
        return i;
    }
}
