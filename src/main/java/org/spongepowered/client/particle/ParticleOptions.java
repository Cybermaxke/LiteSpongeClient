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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;
import java.util.function.Function;

import javax.annotation.Nullable;

/**
 * An enumeration with all the particle options in minecraft.
 */
public final class ParticleOptions {

    private static final Function<PacketBuffer, Vec3d> VEC3D_DESERIALIZER = packetBuffer -> {
        double x = packetBuffer.readFloat();
        double y = packetBuffer.readFloat();
        double z = packetBuffer.readFloat();
        return new Vec3d(x, y, z);
    };

    private static final Function<PacketBuffer, Color> COLOR_DESERIALIZER = packetBuffer -> new Color(packetBuffer.readInt());

    private static final Function<PacketBuffer, ItemStack> ITEM_DESERIALIZER = packetBuffer -> {
        int itemType = packetBuffer.readVarIntFromBuffer();
        short damage = packetBuffer.readShort();
        return new ItemStack(Item.getItemById(itemType), 1, damage);
    };

    private static final Function<PacketBuffer, IBlockState> BLOCK_STATE_DESERIALIZER = packetBuffer ->
            Block.getStateById(packetBuffer.readVarIntFromBuffer());

    private static final Function<PacketBuffer, Byte> BYTE_DESERIALIZER = PacketBuffer::readByte;

    private static final Function<PacketBuffer, Integer> INT_DESERIALIZER = PacketBuffer::readInt;

    private static final Function<PacketBuffer, Float> FLOAT_DESERIALIZER = PacketBuffer::readFloat;

    public static final ParticleOption<Integer> COUNT = new ParticleOption<>(Integer.class, INT_DESERIALIZER);

    public static final ParticleOption<Vec3d> OFFSET = new ParticleOption<>(Vec3d.class, VEC3D_DESERIALIZER);

    public static final ParticleOption<Vec3d> VELOCITY = new ParticleOption<>(Vec3d.class, VEC3D_DESERIALIZER);

    public static final ParticleOption<Color> COLOR = new ParticleOption<>(Color.class, COLOR_DESERIALIZER);

    public static final ParticleOption<Float> SCALE = new ParticleOption<>(Float.class, FLOAT_DESERIALIZER);

    /**
     * The displayed item represented as a item type
     * and a damage value.
     */
    public static final ParticleOption<ItemStack> ITEM = new ParticleOption<>(ItemStack.class, ITEM_DESERIALIZER);

    /**
     * The displayed block state.
     */
    public static final ParticleOption<IBlockState> BLOCK = new ParticleOption<>(IBlockState.class, BLOCK_STATE_DESERIALIZER);

    public static final ParticleOption<Byte> NOTE = new ParticleOption<>(Byte.class, BYTE_DESERIALIZER);

    private final static IntHashMap<ParticleOption<?>> lookup = new IntHashMap<>();
    private static int count;

    /**
     * Registers a {@link ParticleOption} with the specified opcode.
     *
     * @param opcode The opcode
     * @param option The option
     * @param <V> The value type
     */
    public static <V> void register(int opcode, ParticleOption<V> option) {
        checkNotNull(option, "option");
        checkArgument(lookup.lookup(opcode) == null, "The opcode is already in use");

        lookup.addKey(opcode, option);
        count++;
    }

    /**
     * Gets the amount of {@link ParticleOption}s that are registered.
     *
     * @return The count
     */
    public static int getOptionsCount() {
        return count;
    }

    /**
     * Gets a {@link ParticleOptions} for the specified id.
     *
     * @return The particle option
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <V> ParticleOption<V> getOption(int id) {
        return (ParticleOption<V>) lookup.lookup(id);
    }

    static {
        register(0, COUNT);
        register(1, OFFSET);
        register(2, VELOCITY);
        register(3, COLOR);
        register(4, SCALE);
        register(5, ITEM);
        register(6, BLOCK);
        register(7, NOTE);
    }
}
