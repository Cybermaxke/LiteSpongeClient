package me.cybermaxke.spongeliteclient.network;

import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public interface Message {

    void writeTo(PacketBuffer buf) throws IOException;

    void readFrom(PacketBuffer buf) throws IOException;
}
