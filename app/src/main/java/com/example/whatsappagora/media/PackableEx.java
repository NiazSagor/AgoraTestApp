package com.example.whatsappagora.media;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
