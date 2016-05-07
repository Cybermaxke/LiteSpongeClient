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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import net.minecraft.util.IntHashMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

/**
 * The networking message registry.
 */
public class MessageRegistry {

    private final IntHashMap<MessageRegistration<?>> registrationByOpcode = new IntHashMap<>();
    private final Map<Class<?>, MessageRegistration<?>> registrationByType = new HashMap<>();

    private static class MessageRegistrationImpl<M extends Message> implements MessageRegistration<M> {

        private final byte opcode;
        private final Class<M> messageType;
        private final Optional<MessageHandler<? super M>> messageHandler;

        private MessageRegistrationImpl(byte opcode, Class<M> messageType, Optional<MessageHandler<? super M>> messageHandler) {
            this.opcode = opcode;
            this.messageType = messageType;
            this.messageHandler = messageHandler;
        }

        @Override
        public byte getOpcode() {
            return this.opcode;
        }

        @Override
        public Class<M> getType() {
            return this.messageType;
        }

        @Override
        public Optional<MessageHandler<? super M>> getHandler() {
            return this.messageHandler;
        }
    }

    /**
     * Gets the {@link MessageRegistration} for the specified message type if present.
     *
     * @param messageType The message class
     * @param <M> The message type
     * @return The message registration
     */
    public <M extends Message> Optional<MessageRegistration<M>> getRegistration(Class<M> messageType) {
        return Optional.ofNullable((MessageRegistration<M>) this.registrationByType.get(checkNotNull(messageType, "messageType")));
    }

    /**
     * Gets the {@link MessageRegistration} for the specified message type if present.
     *
     * @param opcode The opcode
     * @return The message registration
     */
    public Optional<MessageRegistration<?>> getRegistration(int opcode) {
        return Optional.ofNullable(this.registrationByOpcode.lookup(opcode));
    }

    /**
     * Register a message type with a specific opcode.
     *
     * @param opcode The opcode
     * @param messageType The message class
     * @param <M> The message type
     */
    public <M extends Message> void register(int opcode, Class<M> messageType) {
        this.register0(opcode, messageType, null);
    }

    /**
     * Register a message type with a specific opcode and a handler.
     *
     * @param opcode The opcode
     * @param messageType The message class
     * @param handler The message handler
     * @param <M> The message type
     */
    public <M extends Message> void register(int opcode, Class<M> messageType, MessageHandler<? super M> handler) {
        this.register0(opcode, messageType, checkNotNull(handler, "handler"));
    }

    private <M extends Message> void register0(int opcode, Class<M> messageType, @Nullable MessageHandler<? super M> handler) {
        checkNotNull(messageType, "messageType");
        checkArgument(!this.registrationByType.containsValue(messageType), "The message type is already registered");
        byte opcode0 = (byte) opcode;
        checkArgument(!this.registrationByOpcode.containsItem(opcode0), "The opcode is already used");
        Optional<MessageHandler<? super M>> handlerRegistration = Optional.empty();
        if (handler != null) {
            handlerRegistration = Optional.of(handler);
        }
        MessageRegistration<M> registration = new MessageRegistrationImpl<>(opcode0, messageType, handlerRegistration);
        this.registrationByOpcode.addKey(opcode0, registration);
        this.registrationByType.put(messageType, registration);
    }
}
