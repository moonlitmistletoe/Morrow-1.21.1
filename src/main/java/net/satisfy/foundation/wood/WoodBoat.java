package net.satisfy.foundation.wood;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WoodBoat extends Boat {
    private static final EntityDataAccessor<String> WOOD = SynchedEntityData.defineId(WoodBoat.class, EntityDataSerializers.STRING);

    public WoodBoat(EntityType<? extends Boat> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(WOOD, "");
    }

    public BoatWood getWood() {
        ResourceLocation id = ResourceLocation.tryParse(entityData.get(WOOD));
        BoatWood wood = id == null ? null : BoatWood.get(id);
        if (wood != null) {
            return wood;
        }
        List<BoatWood> own = BoatWood.byNamespace(BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getNamespace());
        return own.isEmpty() ? BoatWood.all().getFirst() : own.getFirst();
    }

    public void setWood(BoatWood wood) {
        entityData.set(WOOD, wood.id().toString());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Type", 8)) {
            String name = tag.getString("Type");
            String namespace = BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getNamespace();
            ResourceLocation id = name.contains(":") ? ResourceLocation.tryParse(name) : ResourceLocation.tryBuild(namespace, name);
            BoatWood wood = id == null ? null : BoatWood.get(id);
            if (wood != null) {
                setWood(wood);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Type", getWood().id().toString());
    }

    @Override
    public @NotNull Item getDropItem() {
        return getWood().item(false);
    }
}
