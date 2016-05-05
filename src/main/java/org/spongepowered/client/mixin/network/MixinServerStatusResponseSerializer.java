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
package org.spongepowered.client.mixin.network;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.ServerStatusResponse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.client.ServerCompatibility;
import org.spongepowered.client.ServerType;
import org.spongepowered.client.SpongeStatusInfo;
import org.spongepowered.client.interfaces.IMixinServerStatusResponse;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Mixin(ServerStatusResponse.Serializer.class)
public abstract class MixinServerStatusResponseSerializer {

    @Inject(method = "deserialize", at = @At("RETURN"))
    private void onDeserialize(JsonElement element, Type type, JsonDeserializationContext ctx, CallbackInfoReturnable<ServerStatusResponse> cir) {
        JsonObject json = element.getAsJsonObject();
        ServerStatusResponse response = cir.getReturnValue();

        ServerType serverType = null;
        ServerCompatibility serverCompat = null;

        if (json.has("spongeInfo")) {
            JsonObject spongeInfo = json.getAsJsonObject("spongeInfo");
            serverType = spongeInfo.has("serverType") ? ServerType.valueOf(spongeInfo.get("serverType").getAsString().toUpperCase()) : null;
            // Mainly means the server compatibility against the vanilla client
            serverCompat = spongeInfo.has("serverCompat") ? ServerCompatibility.valueOf(
                    spongeInfo.get("serverCompat").getAsString().toUpperCase()) : null;
        }

        // System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(json.getAsJsonObject("modinfo")));

        if (serverType == null || serverCompat == null) {
            JsonObject modInfo = json.has("modinfo") ? json.getAsJsonObject("modinfo") : null;
            if (modInfo != null && modInfo.has("modList")) {
                List<String> mods = StreamSupport.stream(modInfo.getAsJsonArray("modList").spliterator(), false)
                        .map(e -> e.getAsJsonObject().get("modid").getAsString())
                        .collect(Collectors.toList());
                if (serverType == null) {
                    if (mods.contains("sponge")) {
                        serverType = ServerType.SPONGE_FORGE;
                    } else {
                        serverType = ServerType.FORGE;
                    }
                }
            } else {
                if (serverCompat == null) {
                    serverCompat = ServerCompatibility.UNKNOWN;
                }
            }
        }
        ((IMixinServerStatusResponse) response).setSpongeInfo(new SpongeStatusInfo(
                serverType == null ? ServerType.VANILLA : serverType, serverCompat == null ? ServerCompatibility.UNKNOWN : serverCompat));
    }
}
