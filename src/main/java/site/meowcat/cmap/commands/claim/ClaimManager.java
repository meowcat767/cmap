package site.meowcat.cmap.commands.claim;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import site.meowcat.cmap.commands.claim.ClaimData;

import java.util.UUID;

public class ClaimManager {

    private static final String DATA_NAME = "claims";

    public static ClaimData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                ClaimData::load,
                ClaimData::new,
                "claims"
        );
    }

    public static boolean claim(ServerPlayer player) {
        ClaimData data = get(player.serverLevel());
        ChunkPos pos = new ChunkPos(player.blockPosition());

        if (data.getClaims().containsKey(pos)) return false;

        data.getClaims().put(pos, player.getUUID());
        data.setDirty();

        return true;
    }

    public static boolean unclaim(ServerPlayer player) {
        ClaimData data = get(player.serverLevel());
        ChunkPos pos = new ChunkPos(player.blockPosition());

        if (!player.getUUID().equals(data.getClaims().get(pos))) return false;

        data.getClaims().remove(pos);
        data.setDirty();

        return true;
    }

    public static boolean canModify(ServerPlayer player, ChunkPos pos) {
        ClaimData data = get(player.serverLevel());
        UUID owner = data.getClaims().get(pos);

        return owner == null || owner.equals(player.getUUID());
    }
}