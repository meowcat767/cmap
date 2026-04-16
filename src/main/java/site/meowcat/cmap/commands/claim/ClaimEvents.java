package site.meowcat.cmap.commands.claim;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class ClaimEvents {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        ChunkPos pos = new ChunkPos(event.getPos());

        if (!ClaimManager.canModify(player, pos)) {
            event.setCanceled(true);
            player.sendSystemMessage(Component.literal("This chunk is claimed!"));
        }
    }
}
