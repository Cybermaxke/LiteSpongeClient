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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.client.ServerCompatibility;
import org.spongepowered.client.ServerType;
import org.spongepowered.client.SpongeStatusInfo;
import org.spongepowered.client.gui.GuiIcons;
import org.spongepowered.client.gui.TooltipIcon;
import org.spongepowered.client.interfaces.IMixinServerData;

@Mixin(ServerListEntryNormal.class)
public abstract class MixinServerListEntryNormal implements GuiListExtended.IGuiListEntry {

    @Final @Shadow private Minecraft mc;
    @Final @Shadow private ServerData server;
    @Final @Shadow private GuiMultiplayer owner;

    @Inject(method = "drawEntry", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/gui/ServerListEntryNormal;drawTextureAt(IILnet/minecraft/util/ResourceLocation;)V"))
    public void onDrawTexture(int slotIndex, int x, int y, int width, int slotHeight, int mouseX, int mouseY, boolean isSelected, CallbackInfo ci) {
        int rx = mouseX - x;
        int ry = mouseY - y;

        TooltipIcon[] icons = null;
        SpongeStatusInfo statusInfo = ((IMixinServerData) this.server).getSpongeInfo();
        ServerType serverType = statusInfo.getServerType();

        if (serverType != ServerType.VANILLA) {
            icons = new TooltipIcon[serverType == ServerType.SPONGE_FORGE ? 2 : 1];
            int index = 0;
            if (serverType == ServerType.SPONGE_FORGE || serverType == ServerType.SPONGE) {
                icons[index++] = new TooltipIcon(GuiIcons.SPONGE_ICON, "Sponge Server");
            }
            if (serverType == ServerType.SPONGE_FORGE || serverType == ServerType.FORGE) {
                ServerCompatibility compat = statusInfo.getCompatibility();
                if (compat == ServerCompatibility.SUCCESS) {
                    icons[index] = new TooltipIcon(GuiIcons.FORGE_SUCCESS_ICON, "Forge Server");
                } else if (compat == ServerCompatibility.FAIL) {
                    icons[index] = new TooltipIcon(GuiIcons.FORGE_FAIL_ICON, "Forge Server\n? Incompatible");
                } else {
                    icons[index] = new TooltipIcon(GuiIcons.FORGE_UNKNOWN_ICON, "Forge Server\n\u27A1 Unknown compatibility");
                }
            }
        }

        if (icons != null) {
            this.mc.getTextureManager().bindTexture(GuiIcons.RESOURCE);
            int xOffset = 0;

            for (TooltipIcon icon : icons) {
                SpongeClientGuiHelper.glDrawTexturedRect(x + width - 22 - xOffset, y + 13, icon.getIcon(), 1f);
                String tooltip = icon.getTooltip();
                if (tooltip != null && rx > width - 23 - xOffset && rx < width - 2 - xOffset && ry > 12 && ry < 30) {
                    this.owner.setHoveringText(tooltip);
                }
                xOffset += 20;
            }
        }
    }
}
