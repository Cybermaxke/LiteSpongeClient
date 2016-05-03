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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.client.LiteModSpongeClient;
import org.spongepowered.client.interfaces.IMixinGuiOverlayDebug;
import org.spongepowered.client.network.types.MessageTrackerDataRequest;
import org.spongepowered.client.tracker.TrackerData;

import java.util.List;

import javax.annotation.Nullable;

@Mixin(GuiOverlayDebug.class)
public abstract class MixinGuiOverlayDebug extends Gui implements IMixinGuiOverlayDebug {

    @Shadow @Final private Minecraft mc;

    @Nullable private TrackerData trackerData;
    private BlockPos cursorPos = new BlockPos(0, 0, 0);

    @Inject(method = "call()Ljava/util/List;", at = @At(value = "RETURN", ordinal = 1))
    public void addOwnerInfo(CallbackInfoReturnable<List<String>> cir) {
        List<String> list = cir.getReturnValue();
        if (this.mc.objectMouseOver != null
                && this.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK
                && this.mc.objectMouseOver.getBlockPos() != null) {
            BlockPos blockPos = this.mc.objectMouseOver.getBlockPos();
            if (!blockPos.equals(this.cursorPos)) {
                LiteModSpongeClient.getInstance().getChannelHandler().sendToServer(
                        new MessageTrackerDataRequest(0, -1, blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            }
            if (this.trackerData != null) {
                list.add("Block Owner: " + orUnknown(this.trackerData.getOwner()));
                list.add("Block Notifier: " + orUnknown(this.trackerData.getNotifier()));
            }
            this.cursorPos = this.mc.objectMouseOver.getBlockPos();
        } else if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
            Entity target = this.mc.objectMouseOver.entityHit;
            BlockPos blockPos = target.getPosition();
            if (!blockPos.equals(this.cursorPos)) {
                LiteModSpongeClient.getInstance().getChannelHandler().sendToServer(
                        new MessageTrackerDataRequest(1, target.getEntityId(), blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            }
            if (this.trackerData != null) {
                list.add("Entity Owner: " + orUnknown(this.trackerData.getOwner()));
                list.add("Entity Notifier: " + orUnknown(this.trackerData.getNotifier()));
            }
            this.cursorPos = blockPos;
        }
    }

    private static String orUnknown(String value) {
        return value == null ? "???" : value;
    }

    @Override
    public void setTrackerData(@Nullable TrackerData trackerData) {
        this.trackerData = trackerData;
    }
}
