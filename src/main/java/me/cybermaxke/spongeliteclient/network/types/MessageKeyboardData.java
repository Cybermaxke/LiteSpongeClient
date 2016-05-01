package me.cybermaxke.spongeliteclient.network.types;

import static com.google.common.base.Preconditions.checkNotNull;

import me.cybermaxke.spongeliteclient.keyboard.KeyBinding;
import me.cybermaxke.spongeliteclient.keyboard.KeyCategory;
import me.cybermaxke.spongeliteclient.network.Message;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class MessageKeyboardData implements Message {

    private Collection<KeyCategory> keyCategories;
    private Collection<KeyBinding> keyBindings;

    public MessageKeyboardData() {
    }

    public Collection<KeyCategory> getKeyCategories() {
        return this.keyCategories;
    }

    public Collection<KeyBinding> getKeyBindings() {
        return this.keyBindings;
    }

    @Override
    public void writeTo(PacketBuffer buf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readFrom(PacketBuffer buf) throws IOException {
        Map<Integer, KeyCategory> categories = new HashMap<>();
        int keyCategoriesCount = buf.readVarIntFromBuffer();
        for (int i = 0; i < keyCategoriesCount; i++) {
            int internalId = buf.readVarIntFromBuffer();
            String id = buf.readStringFromBuffer(256);
            ITextComponent title = buf.readTextComponent();
            categories.put(internalId,  new KeyCategory(id, internalId, title));
        }
        this.keyCategories = new HashSet<>(categories.values());
        int keyBindingCount = buf.readVarIntFromBuffer();
        this.keyBindings = new HashSet<>(keyBindingCount);
        for (int i = 0; i < keyBindingCount; i++) {
            int internalId = buf.readVarIntFromBuffer();
            String id = buf.readStringFromBuffer(256);
            int categoryId = buf.readVarIntFromBuffer();
            ITextComponent displayName = buf.readTextComponent();

            KeyCategory keyCategory = checkNotNull(categories.get(categoryId));
            this.keyBindings.add(new KeyBinding(id, internalId, keyCategory, displayName));
        }
    }
}
