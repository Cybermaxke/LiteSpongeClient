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
package org.spongepowered.client.gui;

import com.mumfrey.liteloader.client.util.render.IconAbsolute;
import net.minecraft.util.ResourceLocation;

public final class GuiIcons {

    public final static ResourceLocation RESOURCE = new ResourceLocation("litespongeclient:textures/gui/icons.png");
    public final static IconAbsolute SPONGE_ICON = new IconAbsolute(RESOURCE, "sponge", 16, 16, 0f, 0f, 64f, 64f);
    public final static IconAbsolute FORGE_SUCCESS_ICON = new IconAbsolute(RESOURCE, "forgeSuccess", 16, 16, 64f, 0f, 128f, 64f);
    public final static IconAbsolute FORGE_FAIL_ICON = new IconAbsolute(RESOURCE, "forgeInvalid", 16, 16, 128f, 0f, 192f, 64f);
    public final static IconAbsolute FORGE_UNKNOWN_ICON = new IconAbsolute(RESOURCE, "forgeUnknown", 16, 16, 192f, 0f, 256f, 64f);
}
