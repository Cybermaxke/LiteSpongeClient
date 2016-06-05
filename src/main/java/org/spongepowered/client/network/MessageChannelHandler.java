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
package org.spongepowered.client.network;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MessageChannelHandler implements MessageDispatcher {

    private final List<String> channels;
    private final MessageRegistry messageRegistry;
    private final String channel;
    private final Logger logger;

    public MessageChannelHandler(MessageRegistry messageRegistry, String channel, Logger logger) {
        this.channels = Collections.singletonList(channel);
        this.messageRegistry = messageRegistry;
        this.channel = channel;
        this.logger = logger;
    }

    /**
     * Gets a singleton list with all the channel of this handler.
     *
     * @return The channel
     */
    public List<String> getChannels() {
        return this.channels;
    }

    /**
     * Handles a custom payload message.
     *
     * @param channel The channel
     * @param data The packet buffer
     */
    public void onCustomPayload(String channel, PacketBuffer data) {
        byte opcode = data.readByte();

        Optional<MessageRegistration<?>> registration = this.messageRegistry.getRegistration(opcode);
        if (registration.isPresent()) {
            MessageHandler handler = registration.get().getHandler().orElse(null);
            if (handler != null) {
                Message message;
                try {
                    message = registration.get().getType().newInstance();
                } catch (Exception e) {
                    this.logger.error("Unable to instantiate the message type {}", registration.get().getType().getName(), e);
                    return;
                }
                try {
                    message.readFrom(data);
                } catch (IOException e) {
                    this.logger.error("Unable to deserialize the message of type {}", registration.get().getType().getName(), e);
                    return;
                }

                NetHandlerPlayClient netHandler = Minecraft.getMinecraft().thePlayer.connection;
                handler.handle(netHandler, message);
            }
        } else {
            this.logger.warn("Received a message with unknown opcode {}", opcode);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void sendToServer(Message message) {
        Optional<MessageRegistration<?>> registration = (Optional) this.messageRegistry.getRegistration(message.getClass());
        if (registration.isPresent()) {
            PacketBuffer content = new PacketBuffer(Unpooled.buffer());

            try {
                message.writeTo(content);
            } catch (IOException e) {
                this.logger.error("Unable to serialize the message of type {}", registration.get().getType().getName(), e);
                return;
            }

            PacketBuffer buf = new PacketBuffer(Unpooled.buffer(content.array().length + 1));
            buf.writeByte(registration.get().getOpcode());
            buf.writeBytes(content);

            CPacketCustomPayload packet = new CPacketCustomPayload(this.channel, buf);
            Minecraft.getMinecraft().thePlayer.connection.sendPacket(packet);
        } else {
            this.logger.warn("Attempted to send a message type {} that wasn't registered", registration.get().getType().getName());
        }
    }

}
