package dev.tonimatas.cerium.bridge.server.rcon;

import java.net.SocketAddress;

public interface RconConsoleSourceBridge {
    void cerium$setSocketAddress(SocketAddress socketAddress);
    
    SocketAddress cerium$getSocketAddress();
}
