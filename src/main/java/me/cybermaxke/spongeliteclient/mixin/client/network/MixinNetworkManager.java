package me.cybermaxke.spongeliteclient.mixin.client.network;

import io.netty.channel.SimpleChannelInboundHandler;
import me.cybermaxke.spongeliteclient.keyboard.KeyboardNetworkHandler;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager extends SimpleChannelInboundHandler<Packet<?>> {

    @Shadow private INetHandler packetListener;

    @Inject(method = "closeChannel", at = @At("RETURN"))
    public void onCloseChannel(ITextComponent message) {
        if (this.packetListener instanceof NetHandlerPlayClient) {
            // Cleanup the keyboard data at logout
            KeyboardNetworkHandler.handleCleanup();
        }
    }
}
