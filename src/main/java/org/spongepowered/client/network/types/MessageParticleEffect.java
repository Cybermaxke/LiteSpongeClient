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
package org.spongepowered.client.network.types;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.client.network.Message;
import org.spongepowered.client.particle.ParticleOption;
import org.spongepowered.client.particle.ParticleOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MessageParticleEffect implements Message {

    private int particleType;
    private Vec3d position;
    private Map<ParticleOption<?>, Object> options;

    public MessageParticleEffect() {
    }

    @Override
    public void writeTo(PacketBuffer buf) throws IOException {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readFrom(PacketBuffer buf) throws IOException {
        this.options = new HashMap<>();
        this.particleType = buf.readShort();

        double x = buf.readFloat();
        double y = buf.readFloat();
        double z = buf.readFloat();
        this.position = new Vec3d(x, y, z);

        /**
         * Keep reading options as long as they are coming, and stop
         * when the client doesn't have any more options that can be used.
         */
        int maxOptions = ParticleOptions.getOptionsCount();
        int startIndex = 0;
        while (startIndex < maxOptions && buf.readableBytes() > 0) {
            byte options = buf.readByte();
            for (int i = 0; i < Byte.SIZE; i++) {
                if ((options & (1 << i)) != 0) {
                    ParticleOption option = ParticleOptions.getOption(startIndex + i);
                    if (option != null) {
                        this.options.put(option, option.getDeserializer().apply(buf));
                    }
                }
            }
            startIndex += Byte.SIZE;
        }
    }

    public Map<ParticleOption<?>, Object> getOptions() {
        return this.options;
    }

    @SuppressWarnings("unchecked")
    public <V> Optional<V> getOption(ParticleOption<V> option) {
        return (Optional) Optional.ofNullable(this.options.get(option));
    }

    public Vec3d getPosition() {
        return this.position;
    }

    public int getParticleType() {
        return this.particleType;
    }

}
