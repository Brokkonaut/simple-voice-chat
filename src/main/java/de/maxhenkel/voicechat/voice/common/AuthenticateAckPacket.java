package de.maxhenkel.voicechat.voice.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class AuthenticateAckPacket implements Packet<AuthenticateAckPacket> {

    public AuthenticateAckPacket() {

    }

    @Override
    public AuthenticateAckPacket fromBytes(DataInputStream buf) {
        AuthenticateAckPacket packet = new AuthenticateAckPacket();
        return packet;
    }

    @Override
    public void toBytes(DataOutputStream buf) {

    }
}
