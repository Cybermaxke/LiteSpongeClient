package me.cybermaxke.spongeliteclient.network;

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
        return Optional.ofNullable((MessageRegistration) this.registrationByType.get(checkNotNull(messageType, "messageType")));
    }

    /**
     * Gets the {@link MessageRegistration} for the specified message type if present.
     *
     * @param opcode The opcode
     * @return The message registration
     */
    public Optional<MessageRegistration<?>> getRegistration(int opcode) {
        return Optional.ofNullable((MessageRegistration) this.registrationByOpcode.lookup(opcode));
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
        checkArgument(!this.registrationByOpcode.containsItem(opcode), "The opcode is already used");
        Optional<MessageHandler<? super M>> handlerRegistration = Optional.empty();
        if (handler != null) {
            handlerRegistration = Optional.of(handler);
        }
        MessageRegistration<M> registration = new MessageRegistrationImpl<>((byte) opcode, messageType, handlerRegistration);
        this.registrationByOpcode.addKey(opcode, registration);
        this.registrationByType.put(messageType, registration);
    }
}
