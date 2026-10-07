package net.satisfy.foundation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.foundation.text.TextEditableBlockEntity;
import org.jetbrains.annotations.NotNull;

public class WallDecorationBlockEntity extends BlockEntity implements TextEditableBlockEntity {
    private static final int LINES = 3;

    private final Component[] text = {Component.empty(), Component.empty(), Component.empty()};
    private boolean glowing;

    public WallDecorationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public Component getText(int line) {
        return text[line];
    }

    @Override
    public void setText(int line, Component component) {
        String value = component.getString();
        if (getBlockState().getBlock() instanceof WallDecorationBlock block && value.length() > block.getMaxLength()) {
            value = value.substring(0, block.getMaxLength());
        }
        text[line] = Component.literal(value);
        sync();
    }

    @Override
    public int getTextLineCount() {
        return LINES;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
        sync();
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        glowing = tag.getBoolean("Glowing");
        for (int i = 0; i < LINES; i++) {
            if (tag.contains("Text" + i)) {
                Component component = Component.Serializer.fromJson(tag.getString("Text" + i), provider);
                text[i] = component != null ? component : Component.empty();
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("Glowing", glowing);
        for (int i = 0; i < LINES; i++) {
            tag.putString("Text" + i, Component.Serializer.toJson(text[i], provider));
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
