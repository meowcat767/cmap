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
        if (last == null) {
            lastChunkMap.put(uuid, current);
            return;
        }

        lastChunkMap.put(uuid, current);

        handleChunkChange(player, current);
    }

    private static void handleChunkChange(ServerPlayer player, ChunkPos pos) {
        ClaimData data = ClaimManager.get(player.serverLevel());

        UUID owner = data.getClaims().get(pos);

        if (owner == null) {
            sendActionBar(player, "Entering: Wilderness");
        } else {
            String name = getPlayerName(player, owner);
            sendActionBar(player, "Entering: " + name + "'s Land");
        }
    }

    public static void sendActionBar(ServerPlayer player, String text) {
        player.displayClientMessage(Component.literal(text), true);
    }

    public static String getPlayerName(ServerPlayer player, UUID uuid) {
        ServerPlayer target = player.server.getPlayerList().getPlayer(uuid);

        if (target != null) return target.getDisplayName().getString();

        return "Unknown"; // fallback (offline player)
    }
}
