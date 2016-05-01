package me.cybermaxke.spongeliteclient.network;

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
                } catch (InstantiationException | IllegalAccessException e) {
                    this.logger.error("Unable to instantiate the message type {}", registration.get().getType().getName(), e);
                    return;
                }
                try {
                    message.readFrom(data);
                } catch (IOException e) {
                    this.logger.error("Unable to deserialize the message of type {}", registration.get().getType().getName(), e);
                    return;
                }

                NetHandlerPlayClient netHandler = Minecraft.getMinecraft().thePlayer.sendQueue;
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

            CPacketCustomPayload packet = new CPacketCustomPayload(this.channel, content);
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(packet);
        } else {
            this.logger.warn("Attempted to send a message type {} that wasn't registered", registration.get().getType().getName());
        }

    }

}
