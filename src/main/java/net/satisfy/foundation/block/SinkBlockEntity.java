package net.satisfy.foundation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.foundation.registry.FoundationBlockEntities;

public class SinkBlockEntity extends BlockEntity {
    public static final int MAX_LEVEL = 3;
    public static final int FILL_TICKS = 60;
    public static final int TURN_TICKS = 8;

    private int waterLevel;
    private boolean open;
    private long toggleTime = -100;
    private int fillTicks;

    public SinkBlockEntity(BlockPos pos, BlockState state) {
        super(FoundationBlockEntities.SINK.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SinkBlockEntity sink) {
        if (state.getValue(SinkBlock.FILLED)) {
            sink.waterLevel = MAX_LEVEL;
            level.setBlock(pos, state.setValue(SinkBlock.FILLED, false), Block.UPDATE_ALL);
            sink.sync();
        }
        if (!sink.open || sink.waterLevel >= MAX_LEVEL) {
            return;
        }
        if (++sink.fillTicks >= FILL_TICKS) {
            sink.fillTicks = 0;
            sink.waterLevel++;
            level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.25F, 1.4F);
            sink.sync();
        }
    }

    public void toggle() {
        if (level == null) {
            return;
        }
        open = !open;
        toggleTime = level.getGameTime();
        fillTicks = 0;
        level.playSound(null, worldPosition.above(), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.4F, open ? 1.2F : 0.9F);
        sync();
    }

    public int getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = Math.max(0, Math.min(MAX_LEVEL, waterLevel));
        sync();
    }

    public boolean isOpen() {
        return open;
    }

    public long getToggleTime() {
        return toggleTime;
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        waterLevel = tag.getInt("WaterLevel");
        open = tag.getBoolean("Open");
        toggleTime = tag.contains("ToggleTime") ? tag.getLong("ToggleTime") : -100;
        fillTicks = tag.getInt("FillTicks");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("WaterLevel", waterLevel);
        tag.putBoolean("Open", open);
        tag.putLong("ToggleTime", toggleTime);
        tag.putInt("FillTicks", fillTicks);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
