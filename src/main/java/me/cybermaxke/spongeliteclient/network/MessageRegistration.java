package me.cybermaxke.spongeliteclient.network;

import java.util.Optional;

public interface MessageRegistration<M extends Message> {

    byte getOpcode();

    Class<M> getType();

    Optional<MessageHandler<? super M>> getHandler();
}
