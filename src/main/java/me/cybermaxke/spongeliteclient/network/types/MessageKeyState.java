package me.cybermaxke.spongeliteclient.network.types;

import me.cybermaxke.spongeliteclient.network.Message;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public final class MessageKeyState implements Message {

    private final int keyBindingId;
    private final boolean state;

    public MessageKeyState(int keyBindingId, boolean state) {
        this.keyBindingId = keyBindingId;
        this.state = state;
    }

    @Override
    public void writeTo(PacketBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(this.keyBindingId);
        buf.writeBoolean(this.state);
    }

    @Override
    public void readFrom(PacketBuffer buf) throws IOException {
        throw new UnsupportedOperationException();
    }
}
