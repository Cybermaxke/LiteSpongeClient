package me.cybermaxke.spongeliteclient.keyboard;

import net.minecraft.util.text.ITextComponent;

public final class KeyCategory {

    private final String id;
    private final int internalId;
    private final ITextComponent title;

    public KeyCategory(String id, int internalId, ITextComponent title) {
        this.internalId = internalId;
        this.title = title;
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public int getInternalId() {
        return this.internalId;
    }

    public ITextComponent getTitle() {
        return this.title;
    }
}
