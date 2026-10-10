package net.moonlitmistletoe.morrow;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Morrow.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> DORIME_AMENO =
            SOUND_EVENTS.register(
                    "dorime-ameno",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    Morrow.MOD_ID,
                                    "dorime-ameno"
                            )
                    )
            );

    public static final DeferredHolder<SoundEvent, SoundEvent> WORLD_OF_LIES =
            SOUND_EVENTS.register(
                    "worldoflies",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    Morrow.MOD_ID,
                                    "worldoflies"
                            )
                    )
            );

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}