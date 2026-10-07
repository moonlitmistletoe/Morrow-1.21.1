package net.satisfy.foundation.text;

import net.minecraft.network.chat.Component;

/** Implement on block entities that accept a {@link SetTextPacket}. */
public interface TextEditableBlockEntity {
    void setText(int line, Component text);
    int getTextLineCount();
}