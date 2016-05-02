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
package org.spongepowered.client.mixin.client.gui;

import com.mumfrey.liteloader.client.gui.SpongeClientGuiHelper;
import com.mumfrey.liteloader.util.render.Icon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.client.interfaces.IMixinServerData;
import org.spongepowered.client.textures.GuiIcons;

@Mixin(ServerListEntryNormal.class)
public abstract class MixinServerListEntryNormal implements GuiListExtended.IGuiListEntry {

    @Final @Shadow private Minecraft mc;
    @Final @Shadow private ServerData server;

    @Inject(method = "drawEntry", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/gui/ServerListEntryNormal;drawTextureAt(IILnet/minecraft/util/ResourceLocation;)V"))
    public void onDrawTexture(int slotIndex, int x, int y, int width, int slotHeight, int mouseX, int mouseY, boolean isSelected, CallbackInfo ci) {
        int rx = mouseX - x;
        int ry = mouseY - y;

        this.mc.getTextureManager().bindTexture(GuiIcons.RESOURCE);

        Icon icon = null;
        switch (((IMixinServerData) this.server).getServerType()) {
            case SPONGE_MODDED:
                // TODO
            case SPONGE:
                icon = GuiIcons.SPONGE_ICON;
                break;
            case MODDED:
                // TODO
                break;
        }

        if (icon != null) {
            SpongeClientGuiHelper.glDrawTexturedRect(x + width - 20, y + 13, icon, 1f);

            if (rx > width - 15 && rx < width && ry > 10 && ry < 26) {
                // Tooltip
            }
        }
    }
}
