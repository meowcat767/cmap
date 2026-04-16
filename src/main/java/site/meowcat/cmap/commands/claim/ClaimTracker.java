package site.meowcat.cmap.commands.claim;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class ClaimTracker {
    private static final Map<UUID, ChunkPos> lastChunkMap = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        ChunkPos current = new ChunkPos(player.blockPosition());
        UUID uuid = player.getUUID();

        ChunkPos last = lastChunkMap.get(uuid);

        // only run when chunk changes
        if (current.equals(last)) return;

        lastChunkMap.put(uuid, current);

        handleChunkChange(player, current);
    }

    private static void handleChunkChange(ServerPlayer player, ChunkPos pos) {
        ClaimData data = ClaimManager.get(player.serverLevel());

        Claim claim = data.getClaims().get(pos);

        if (claim == null) {
            sendActionBar(player, "Entering: Wilderness");
        } else {
            String name = claim.getName();
            sendActionBar(player, "Entering: " + name);
        }
    }

    public static void sendActionBar(ServerPlayer player, String text) {
        player.displayClientMessage(Component.literal(text), true);
    }
}
