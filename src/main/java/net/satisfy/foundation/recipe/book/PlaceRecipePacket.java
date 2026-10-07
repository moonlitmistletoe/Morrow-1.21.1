package net.satisfy.foundation.recipe.book;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.satisfy.foundation.Foundation;
import net.satisfy.foundation.recipe.RecipeUnlockManager;

/** Client -> server: recipe clicked in the station recipe book, fill the inputs. */
public record PlaceRecipePacket(int containerId, ResourceLocation recipe, boolean max) implements CustomPacketPayload {
    public static final Type<PlaceRecipePacket> TYPE = new Type<>(Foundation.identifier("place_recipe"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlaceRecipePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PlaceRecipePacket::containerId,
            ResourceLocation.STREAM_CODEC, PlaceRecipePacket::recipe,
            ByteBufCodecs.BOOL, PlaceRecipePacket::max,
            PlaceRecipePacket::new);

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.c2s(), TYPE, CODEC, (packet, context) -> context.queue(() -> {
            if (context.getPlayer() instanceof ServerPlayer player) {
                handle(packet, player);
            }
        }));
    }

    private static void handle(PlaceRecipePacket packet, ServerPlayer player) {
        AbstractContainerMenu menu = player.containerMenu;
        if (menu.containerId != packet.containerId || !(menu instanceof StationRecipeBookMenu book) || !menu.stillValid(player)) {
            return;
        }
        RecipeHolder<?> recipe = player.serverLevel().getRecipeManager().byKey(packet.recipe).orElse(null);
        if (recipe == null || !book.recipeBookTypes().contains(recipe.value().getType())) {
            return;
        }
        if (book.recipeBookRequiresUnlock() && !RecipeUnlockManager.isUnlocked(player, recipe)) {
            return;
        }
        RecipePlacer.place(player.getInventory(), menu, book.recipeBookPlacementSlots(recipe), book.recipeBookPlacementIngredients(recipe), packet.max);
        menu.broadcastChanges();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
