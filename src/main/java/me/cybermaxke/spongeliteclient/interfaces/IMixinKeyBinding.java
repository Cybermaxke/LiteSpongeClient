package me.cybermaxke.spongeliteclient.interfaces;

import me.cybermaxke.spongeliteclient.keyboard.IClientKeyBinding;

public interface IMixinKeyBinding extends IClientKeyBinding {

    void setInternalId(int internalId);

    void setPressed(boolean pressed);

    void setPressTime(int time);

    /**
     * Removed this key binding from the internal array.
     */
    // void remove(); // SpongeForge
}
