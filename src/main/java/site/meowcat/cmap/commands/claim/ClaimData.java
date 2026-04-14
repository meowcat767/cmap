package site.meowcat.cmap.commands.claim;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClaimData {
    private final Map<ChunkPos, UUID> claims = new HashMap<>();

    public static ClaimData load(CompoundTag  tag) {
        ClaimData data = new ClaimData();
        ListTag list = tag.getList("claims", ListTag.TAG_COMPOUND);

        for (Tag t : list) {
            CompoundTag entry = new CompoundTag();
            ChunkPos pos = new ChunkPos(entry.getInt("x"), entry.getInt("z"));
            UUID owner = entry.getUUID("owner");
            data.claims.put(pos, owner);
        }
        return data;
    }


    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Map.Entry<ChunkPos, UUID> entry : claims.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putInt("x", entry.getKey().x);
            entryTag.putInt("z", entry.getKey().z);
            entryTag.putUUID("owner", entry.getValue());
            list.add(entryTag);
        }
        tag.put("claims", list);
        return tag;
    }

    public Map<ChunkPos, UUID> getClaims() {
        return claims;
    }
}
