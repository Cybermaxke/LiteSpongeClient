package me.cybermaxke.spongeliteclient.keyboard;

import net.minecraft.util.text.ITextComponent;

public final class KeyBinding {

    private final String id;
    private final int internalId;
    private final KeyCategory keyCategory;
    private final ITextComponent displayName;

    public KeyBinding(String id, int internalId, KeyCategory keyCategory, ITextComponent displayName) {
        this.keyCategory = keyCategory;
        this.displayName = displayName;
        this.internalId = internalId;
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public int getInternalId() {
        return this.internalId;
    }

    /**
     * Gets the {@link KeyCategory} of this key binding.
     *
     * @return The key category
     */
    public KeyCategory getKeyCategory() {
        return this.keyCategory;
    }

    /**
     * Gets the display name of this key binding.
     *
     * @return The display name
     */
    public ITextComponent getDisplayName() {
        return this.displayName;
    }
}
