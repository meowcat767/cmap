package site.meowcat.cmap.commands.claim;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClaimData extends SavedData {

    private final Map<ChunkPos, UUID> claims = new HashMap<>();

    public ClaimData() {}

    public static ClaimData load(CompoundTag tag) {
        ClaimData data = new ClaimData();

        ListTag list = tag.getList("claims", Tag.TAG_COMPOUND);
        for (Tag t : list) {
            CompoundTag entry = (CompoundTag) t;
            ChunkPos pos = new ChunkPos(entry.getInt("x"), entry.getInt("z"));
            UUID owner = entry.getUUID("owner");
            data.claims.put(pos, owner);
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();

        for (Map.Entry<ChunkPos, UUID> entry : claims.entrySet()) {
            CompoundTag t = new CompoundTag();
            t.putInt("x", entry.getKey().x);
            t.putInt("z", entry.getKey().z);
            t.putUUID("owner", entry.getValue());
            list.add(t);
        }

        tag.put("claims", list);
        return tag;
    }

    public Map<ChunkPos, UUID> getClaims() {
        return claims;
    }
}
