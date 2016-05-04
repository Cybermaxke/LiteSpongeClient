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

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.client.network.types.MessageParticleEffect;

import java.awt.Color;
import java.util.Random;

public class ParticleEffectNetworkHandler {

    private static final Random random = new Random();

    public static void handleParticleEffect(INetHandlerPlayClient handler, MessageParticleEffect message) {
        WorldClient worldClient = Minecraft.getMinecraft().theWorld;
        if (worldClient == null) {
            return;
        }

        int particleType = message.getParticleType();
        int count = message.getOption(ParticleOptions.COUNT).orElse(1);

        EnumParticleTypes enumParticleType = EnumParticleTypes.getParticleFromId(particleType);

        Vec3d position = message.getPosition();
        double x = position.xCoord;
        double y = position.yCoord;
        double z = position.zCoord;

        float f0 = 0f;
        float f1 = 0f;
        float f2 = 0f;

        int[] extra = null;

        if (enumParticleType == EnumParticleTypes.BLOCK_CRACK ||
                enumParticleType == EnumParticleTypes.BLOCK_DUST) {
            IBlockState blockState = message.getOption(ParticleOptions.BLOCK).orElse(null);
            if (blockState != null) {
                extra = new int[] { Block.getStateId(blockState) };
            } else {
                ItemStack itemStack = message.getOption(ParticleOptions.ITEM).orElse(null);
                if (itemStack != null) {
                    Item itemType = itemStack.getItem();
                    if (itemType instanceof ItemBlock) {
                        extra = new int[] { Block.getStateId(((ItemBlock) itemType).getBlock().getStateFromMeta(itemStack.getItemDamage())) };
                    } else {
                        return;
                    }
                } else {
                    extra = new int[] { Block.getStateId(Blocks.STONE.getDefaultState()) };
                }
            }
        } else if (enumParticleType == EnumParticleTypes.ITEM_CRACK) {
            ItemStack itemStack = message.getOption(ParticleOptions.ITEM).orElse(null);
            if (itemStack != null) {
                extra = new int[] { Item.getIdFromItem(itemStack.getItem()), itemStack.getItemDamage() };
            } else {
                IBlockState blockState = message.getOption(ParticleOptions.BLOCK).orElse(null);
                if (blockState != null) {
                    Item item = Item.getItemFromBlock(blockState.getBlock());
                    if (item != null) {
                        extra = new int[] { Item.getIdFromItem(item), blockState.getBlock().getMetaFromState(blockState) };
                    } else {
                        return;
                    }
                } else {
                    extra = new int[] { Item.getIdFromItem(Item.getItemFromBlock(Blocks.STONE)), 0 };
                }
            }
        }

        if (extra == null) {
            extra = new int[0];
        }

        if (enumParticleType == EnumParticleTypes.REDSTONE ||
                enumParticleType == EnumParticleTypes.SPELL_MOB ||
                enumParticleType == EnumParticleTypes.SPELL_MOB_AMBIENT) {
            Color color = message.getOption(ParticleOptions.COLOR).orElse(null);

            if (color != null) {
                f0 = color.getRed() / 255f;
                f1 = color.getGreen() / 255f;
                f2 = color.getBlue() / 255f;

                if (f0 == 0f && enumParticleType == EnumParticleTypes.REDSTONE) {
                    f0 = 0.00001f;
                }
            }
        } else if (enumParticleType == EnumParticleTypes.EXPLOSION_LARGE ||
                enumParticleType == EnumParticleTypes.SWEEP_ATTACK) {
            f0 = (-message.getOption(ParticleOptions.SCALE).orElse(1f) * 2f) + 2f;
        } else if (enumParticleType == EnumParticleTypes.NOTE) {
            f0 = message.getOption(ParticleOptions.NOTE).map(note -> note / 24f).orElse(0f);
        } else {
            // TODO: Check for particles with velocity
            Vec3d velocity = message.getOption(ParticleOptions.VELOCITY).orElse(null);
            if (velocity != null) {
                f0 = (float) velocity.xCoord;
                f1 = (float) velocity.yCoord;
                f2 = (float) velocity.zCoord;

                // The y value won't work for this effect, if the value isn't 0 the velocity won't work
                if (enumParticleType == EnumParticleTypes.WATER_SPLASH) {
                    f1 = 0f;
                }
            }
        }

        Vec3d offset = message.getOption(ParticleOptions.OFFSET).orElse(null);
        if (offset == null || offset.equals(Vec3d.ZERO)) {
            for (int i = 0; i < count; i++) {
                worldClient.spawnParticle(enumParticleType, true, x, y, z, f0, f1, f2, extra);
            }
        } else {
            for (int i = 0; i < count; i++) {
                double x1 = x + random.nextGaussian() * offset.xCoord;
                double y1 = y + random.nextGaussian() * offset.yCoord;
                double z1 = z + random.nextGaussian() * offset.zCoord;

                worldClient.spawnParticle(enumParticleType, true, x1, y1, z1, f0, f1, f2, extra);
            }
        }
    }
}
