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
package org.spongepowered.client.network.types;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.client.keyboard.KeyBinding;
import org.spongepowered.client.keyboard.KeyCategory;
import org.spongepowered.client.network.Message;
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
            categories.put(internalId, new KeyCategory(id, internalId, title));
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
