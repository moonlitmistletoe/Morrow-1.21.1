package net.satisfy.foundation.util;

import io.netty.buffer.Unpooled;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.*;
import java.util.*;

/** Grab bag of small helpers, stuff that did not fit anywhere else. */
public final class LibUtil {
    private LibUtil() {
    }

    private static final String BLOCK_POS_KEY = "block_pos";

    private static final String BLOCK_POSES_KEY = "block_poses";

    /** Players that have this chunk loaded. */
    public static Collection<ServerPlayer> tracking(ServerLevel world, ChunkPos pos) {
        Objects.requireNonNull(world, "The world cannot be null");
        Objects.requireNonNull(pos, "The chunk pos cannot be null");
        return world.getChunkSource().chunkMap.getPlayers(pos, false);
    }

    public static Collection<ServerPlayer> tracking(ServerLevel world, BlockPos pos) {
        Objects.requireNonNull(pos, "BlockPos cannot be null");
        return tracking(world, new ChunkPos(pos));
    }

    /** 50 -> 0.5f */
    public static float getInPercent(int i) {
        return (float)i / 100.0F;
    }

    /** After eating/drinking: gives {@code returnItem} back (bowl, bottle...) and awards stats. */
    public static ItemStack convertStackAfterFinishUsing(LivingEntity entity, ItemStack used, Item returnItem, Item usedItem) {
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, used);
            serverPlayer.awardStat(Stats.ITEM_USED.get(usedItem));
        }

        if (used.isEmpty()) {
            return new ItemStack(returnItem);
        } else {
            if (entity instanceof Player) {
                Player player = (Player)entity;
                if (!((Player)entity).getAbilities().instabuild) {
                    ItemStack itemStack2 = new ItemStack(returnItem);
                    if (!player.getInventory().add(itemStack2)) {
                        player.drop(itemStack2, false);
                    }
                }
            }

            return used;
        }
    }

    /** Fills a bucket from a block and swaps the block state. */
    public static InteractionResult fillBucket(Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack, ItemStack returnItem, BlockState blockState, SoundEvent soundEvent) {
        if (!level.isClientSide) {
            Item item = itemStack.getItem();
            player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, returnItem));
            player.awardStat(Stats.ITEM_USED.get(item));
            level.setBlockAndUpdate(blockPos, blockState);
            level.playSound((Player)null, blockPos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent((Entity)null, GameEvent.FLUID_PICKUP, blockPos);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Empties a bucket into a block and swaps the block state. */
    public static InteractionResult emptyBucket(Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack, ItemStack returnItem, BlockState blockState, SoundEvent soundEvent) {
        if (!level.isClientSide) {
            Item item = itemStack.getItem();
            player.setItemInHand(interactionHand, ItemUtils.createFilledResult(itemStack, player, returnItem));
            player.awardStat(Stats.ITEM_USED.get(item));
            level.setBlockAndUpdate(blockPos, blockState);
            level.playSound((Player)null, blockPos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent((Entity)null, GameEvent.FLUID_PLACE, blockPos);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** New log block with oak log properties. */
    public static RotatedPillarBlock logBlock() {
        return new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG));
    }

    public static boolean isDamageType(DamageSource source, List<ResourceKey<DamageType>> damageTypes) {
        Iterator var2 = damageTypes.iterator();

        ResourceKey key;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            key = (ResourceKey)var2.next();
        } while(!source.is(key));

        return true;
    }

    /** @return true for any kind of fire damage */
    public static boolean isFire(DamageSource source) {
        return isDamageType(source, List.of(DamageTypes.ON_FIRE, DamageTypes.IN_FIRE, DamageTypes.FIREBALL, DamageTypes.FIREWORKS, DamageTypes.UNATTRIBUTED_FIREBALL));
    }

    public static boolean isIndexInRange(int index, int startInclusive, int endInclusive) {
        return index >= startInclusive && index <= endInclusive;
    }

    public static FriendlyByteBuf create() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    /** Drops the item out of the given face of the block, respecting the collision shape. */
    public static void popResourceFromFace(Level level, BlockPos blockPos, Direction side, ItemStack itemStack) {
        BlockState blockState = level.getBlockState(blockPos);
        double itemWidth = (double) EntityType.ITEM.getWidth();
        double itemHeight = (double)EntityType.ITEM.getHeight();
        VoxelShape shape = blockState.getCollisionShape(level, blockPos);
        double posX = (double)blockPos.getX() + 0.5;
        double posY = (double)blockPos.getY() + 0.5;
        double posZ = (double)blockPos.getZ() + 0.5;
        double offsetX = 0.0;
        double offsetY = 0.0;
        double offsetZ = 0.0;
        switch (side) {
            case DOWN:
                posY = (double)blockPos.getY() - shape.min(Direction.Axis.Y);
                offsetY = -itemHeight * 2.0;
                break;
            case UP:
                posY = (double)blockPos.getY() + shape.max(Direction.Axis.Y);
                break;
            case NORTH:
                posZ = (double)blockPos.getZ() + shape.min(Direction.Axis.Z);
                offsetZ = -itemWidth;
                break;
            case SOUTH:
                posZ = (double)blockPos.getZ() + shape.max(Direction.Axis.Z);
                offsetZ = itemWidth;
                break;
            case WEST:
                posX = (double)blockPos.getX() + shape.min(Direction.Axis.X);
                offsetX = -itemWidth;
                break;
            case EAST:
                posX = (double)blockPos.getX() + shape.max(Direction.Axis.X);
                offsetX = itemWidth;
        }

        int i = side.getStepX();
        int j = side.getStepY();
        int k = side.getStepZ();
        double deltaX = i == 0 ? Mth.nextDouble(level.random, -0.1, 0.1) : (double)i * 0.1;
        double deltaY = j == 0 ? Mth.nextDouble(level.random, 0.0, 0.1) : (double)j * 0.1 + 0.1;
        double deltaZ = k == 0 ? Mth.nextDouble(level.random, -0.1, 0.1) : (double)k * 0.1;
        popResource(level, new ItemEntity(level, posX + offsetX, posY + offsetY, posZ + offsetZ, itemStack, deltaX, deltaY, deltaZ), itemStack);
    }

    private static void popResource(Level level, ItemEntity itemEntity, ItemStack itemStack) {
        if (!level.isClientSide && !itemStack.isEmpty() && level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }

    }

    /** Stores a pos as int array under {@code block_pos}. */
    public static void putBlockPos(CompoundTag compoundTag, BlockPos blockPos) {
        if (blockPos != null) {
            int[] positions = new int[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()};
            compoundTag.putIntArray("block_pos", positions);
        }
    }

    /** Stores multiple positions flat into one int array. */
    public static void putBlockPoses(CompoundTag compoundTag, Collection<BlockPos> blockPoses) {
        if (blockPoses != null && !blockPoses.isEmpty()) {
            int[] positions = new int[blockPoses.size() * 3];
            int pos = 0;

            for(Iterator var4 = blockPoses.iterator(); var4.hasNext(); ++pos) {
                BlockPos blockPos = (BlockPos)var4.next();
                positions[pos * 3] = blockPos.getX();
                positions[pos * 3 + 1] = blockPos.getY();
                positions[pos * 3 + 2] = blockPos.getZ();
            }

            compoundTag.putIntArray("block_poses", positions);
        }
    }

    public static @Nullable BlockPos readBlockPos(CompoundTag compoundTag) {
        if (!compoundTag.contains("block_pos")) {
            return null;
        } else {
            int[] positions = compoundTag.getIntArray("block_pos");
            return new BlockPos(positions[0], positions[1], positions[2]);
        }
    }

    public static Set<BlockPos> readBlockPoses(CompoundTag compoundTag) {
        Set<BlockPos> blockSet = new HashSet<>();
        if (!compoundTag.contains("block_poses")) {
            return blockSet;
        } else {
            int[] positions = compoundTag.getIntArray("block_poses");

            for(int pos = 0; pos < positions.length / 3; ++pos) {
                blockSet.add(new BlockPos(positions[pos * 3], positions[pos * 3 + 1], positions[pos * 3 + 2]));
            }

            return blockSet;
        }
    }
}
