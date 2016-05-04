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
package org.spongepowered.client.particle;

import net.minecraft.util.IntHashMap;

import javax.annotation.Nullable;

/**
 * An enumeration with all the particle options in minecraft.
 */
public enum ParticleOption {
    COUNT,
    OFFSET,
    VELOCITY,
    COLOR,
    SCALE,
    /**
     * The displayed item represented as a item type
     * and a damage value.
     */
    ITEM,
    /**
     * The displayed block state.
     */
    BLOCK,
    NOTE,
    ;

    private final static IntHashMap<ParticleOption> lookup = new IntHashMap<>();
    private final static int count = ParticleOption.values().length;

    public static int getOptionsCount() {
        return count;
    }

    /**
     * Gets a {@link ParticleOption} for the specified id.
     *
     * @return The particle option
     */
    @Nullable
    public static ParticleOption getOption(int id) {
        return lookup.lookup(id);
    }

    static {
        for (ParticleOption option : values()) {
            lookup.addKey(option.ordinal(), option);
        }
    }
}
