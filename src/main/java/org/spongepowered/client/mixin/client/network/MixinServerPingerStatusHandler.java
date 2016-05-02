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
package org.spongepowered.client.mixin.client.network;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.server.SPacketServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.client.ServerType;
import org.spongepowered.client.interfaces.IMixinServerData;
import org.spongepowered.client.interfaces.IMixinServerStatusResponse;

@Mixin(targets = "net.minecraft.client.network.ServerPinger$1")
public abstract class MixinServerPingerStatusHandler implements INetHandlerStatusClient {

    @Shadow(aliases = "b") private ServerData val$server;

    @Inject(method = "handleServerInfo(Lnet/minecraft/network/status/server/SPacketServerInfo;)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/network/status/server/SPacketServerInfo;getResponse()Lnet/minecraft/network/ServerStatusResponse;"))
    private void onHandleInfo(SPacketServerInfo packet, CallbackInfo ci) {
        IMixinServerStatusResponse response = (IMixinServerStatusResponse) packet.getResponse();
        IMixinServerData serverData = (IMixinServerData) this.val$server;

        boolean modded = response.isModded();
        boolean sponge = response.isSponge();

        if (modded && sponge) {
            serverData.setServerType(ServerType.SPONGE_MODDED);
        } else if (sponge) {
            serverData.setServerType(ServerType.SPONGE);
        } else if (modded) {
            serverData.setServerType(ServerType.MODDED);
        } else {
            serverData.setServerType(ServerType.VANILLA);
        }
    }
}
