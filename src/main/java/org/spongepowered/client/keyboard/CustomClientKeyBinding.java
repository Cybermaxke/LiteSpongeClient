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

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.client.LiteModSpongeClient;
import org.spongepowered.client.interfaces.IMixinKeyBinding;

import java.io.IOException;

import javax.annotation.Nullable;

public class CustomClientKeyBinding extends net.minecraft.client.settings.KeyBinding implements IClientKeyBinding {

    private final String id;

    private ITextComponent displayName;
    @Nullable private ITextComponent categoryTitle;
    @Nullable private String categoryTranslationKey;

    /**
     * Creates a new custom key binding for the
     * specified key binding settings.
     *
     * @param keyBinding The key binding
     */
    public CustomClientKeyBinding(KeyBinding keyBinding) {
        super(keyBinding.getDisplayName().getUnformattedText(), 0, keyBinding.getKeyCategory().getTitle()
                .map(ITextComponent::getUnformattedText).orElseGet(() -> keyBinding.getKeyCategory().getTranslatableTitle().get()));
        ((IMixinKeyBinding) this).setInternalId(keyBinding.getInternalId());
        this.id = keyBinding.getId();
        this.displayName = keyBinding.getDisplayName();
        this.categoryTitle = keyBinding.getKeyCategory().getTitle().orElse(null);
        this.categoryTranslationKey = keyBinding.getKeyCategory().getTranslatableTitle().orElse(null);
    }

    public void setKeyCodeWithoutSave(int keyCode) {
        super.setKeyCode(keyCode);
    }

    @Override
    public void setKeyCode(int keyCode) {
        int oldKeyCode = this.getKeyCode();
        super.setKeyCode(keyCode);
        if (oldKeyCode != keyCode) {
            KeyBindingStorage storage = LiteModSpongeClient.getInstance().getKeyBindingStorage();
            storage.putKeyCode(this.id, keyCode);
            try {
                storage.save();
            } catch (IOException e) {
                LiteModSpongeClient.getInstance().getLogger().error("An error occurred while loading the key bindings file", e);
            }
        }
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
    public String getKeyCategory() {
        if (this.categoryTitle != null) {
            return this.categoryTitle.getUnformattedText();
        }
        return this.categoryTranslationKey;
    }

    @Override
    public String getFormattedCategory() {
        if (this.categoryTitle != null) {
            return this.categoryTitle.getUnformattedText();
        }
        return I18n.format(this.categoryTranslationKey);
    }

    @Override
    public String getFormattedDisplayName() {
        return this.displayName.getUnformattedText();
    }
}
