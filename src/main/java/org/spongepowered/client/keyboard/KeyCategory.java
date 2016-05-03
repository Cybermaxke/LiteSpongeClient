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

import net.minecraft.util.IntHashMap;
import net.minecraft.util.text.ITextComponent;

import java.util.Optional;

import javax.annotation.Nullable;

public final class KeyCategory {

    public static final IntHashMap<KeyCategory> DEFAULT_CATEGORIES = new IntHashMap<>();

    static {
        DEFAULT_CATEGORIES.addKey(0, new KeyCategory("minecraft:movement", 0, "key.categories.movement"));
        DEFAULT_CATEGORIES.addKey(1, new KeyCategory("minecraft:inventory", 1, "key.categories.inventory"));
        DEFAULT_CATEGORIES.addKey(2, new KeyCategory("minecraft:gameplay", 2, "key.categories.gameplay"));
        DEFAULT_CATEGORIES.addKey(3, new KeyCategory("minecraft:multiplayer", 3, "key.categories.multiplayer"));
        DEFAULT_CATEGORIES.addKey(4, new KeyCategory("minecraft:misc", 4, "key.categories.misc"));
    }

    private final String id;
    private final int internalId;
    @Nullable private final ITextComponent title;
    @Nullable private final String translationKey;

    public KeyCategory(String id, int internalId, ITextComponent title) {
        this(id, internalId, title, null);
    }

    private KeyCategory(String id, int internalId, String translationKey) {
        this(id, internalId, null, translationKey);
    }

    private KeyCategory(String id, int internalId, @Nullable ITextComponent title, @Nullable String translationKey) {
        this.translationKey = translationKey;
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

    public Optional<ITextComponent> getTitle() {
        return Optional.ofNullable(this.title);
    }

    public Optional<String> getTranslatableTitle() {
        return Optional.ofNullable(this.translationKey);
    }
}
