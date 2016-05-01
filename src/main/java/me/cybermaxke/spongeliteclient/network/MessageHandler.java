package me.cybermaxke.spongeliteclient.network;

import net.minecraft.network.play.INetHandlerPlayClient;

public interface MessageHandler<M extends Message> {

    void handle(INetHandlerPlayClient handler, M message);
}
