package de.maxhenkel.voicechat.net;

import de.maxhenkel.voicechat.Voicechat;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class RequestPlayerListPacket implements Packet<RequestPlayerListPacket> {

    public static final String REQUEST_PLAYER_LIST = Voicechat.MODID + ":" + "request_player_list";

    public RequestPlayerListPacket() {

    }

    @Override
    public String getID() {
        return REQUEST_PLAYER_LIST;
    }

    @Override
    public RequestPlayerListPacket fromBytes(DataInputStream buf) {

        return this;
    }

    @Override
    public void toBytes(DataOutputStream buf) {

    }
}
