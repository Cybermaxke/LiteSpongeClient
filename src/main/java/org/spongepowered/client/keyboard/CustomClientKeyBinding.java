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

import org.spongepowered.client.interfaces.IMixinKeyBinding;
import net.minecraft.util.text.ITextComponent;

public class CustomClientKeyBinding extends net.minecraft.client.settings.KeyBinding implements IClientKeyBinding {

    private final String id;

    private ITextComponent displayName;
    private ITextComponent categoryTitle;

    /**
     * Creates a new custom key binding for the
     * specified key binding settings.
     *
     * @param keyBinding The key binding
     */
    public CustomClientKeyBinding(KeyBinding keyBinding) {
        super(keyBinding.getDisplayName().getUnformattedText(), 0, keyBinding.getKeyCategory().getTitle().getUnformattedText());
        ((IMixinKeyBinding) this).setInternalId(keyBinding.getInternalId());
        this.id = keyBinding.getId();
        this.displayName = keyBinding.getDisplayName();
        this.categoryTitle = keyBinding.getKeyCategory().getTitle();
    }

    /**
     * Gets the identifier of the custom key binding.
     *
     * @return The identifier
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the display name of the key binding.
     *
     * @return The display name
     */
    @Override
    public String getKeyDescription() {
        return this.displayName.getUnformattedText();
    }

    @Override
    public String getCategory() {
        return this.categoryTitle.getUnformattedText();
    }

    @Override
    public String getFormattedCategory() {
        return this.categoryTitle.getUnformattedText();
    }

    @Override
    public String getDisplayName() {
        return this.displayName.getUnformattedText();
    }

    @Override
    public String getFormattedDisplayName() {
        return this.displayName.getUnformattedText();
    }
}
