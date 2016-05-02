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
import org.spongepowered.client.interfaces.IMixinServerStatusResponse;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Mixin(ServerStatusResponse.Serializer.class)
public abstract class MixinServerStatusResponseSerializer {

    @Inject(method = "deserialize", at = @At("RETURN"))
    private void onDeserialize(JsonElement element, Type type, JsonDeserializationContext ctx, CallbackInfoReturnable<ServerStatusResponse> cir) {
        JsonObject json = element.getAsJsonObject();
        JsonObject modInfo = json.has("modinfo") ? json.getAsJsonObject("modinfo") : null;

        ServerStatusResponse response = cir.getReturnValue();
        List<String> mods = Collections.emptyList();

        if (modInfo != null && modInfo.has("modList")) {
            mods = StreamSupport.stream(json.getAsJsonArray("modList").spliterator(), false)
                    .map(e -> e.getAsJsonObject().get("modid").getAsString())
                    .collect(Collectors.toList());
        }

        if (json.has("sponge")) {
            ((IMixinServerStatusResponse) response).setSponge(json.get("sponge").getAsBoolean());
        } else if (!mods.isEmpty()) {
            ((IMixinServerStatusResponse) response).setSponge(mods.contains("SpongeForge"));
        }

        if (json.has("modded")) {
            ((IMixinServerStatusResponse) response).setModded(json.get("modded").getAsBoolean());
        } else {
            mods.remove("FML");
            mods.remove("SpongeForge");
            ((IMixinServerStatusResponse) response).setModded(mods.size() > 0);
        }
    }
}
