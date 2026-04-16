package site.meowcat.cmap.commands.claim;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ClaimManager {

    private static final String DATA_NAME = "claims";

    public static ClaimData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                ClaimData::load,
                ClaimData::new,
                DATA_NAME
        );
    }

    public static boolean claim(ServerPlayer player) {
        ClaimData data = get(player.serverLevel());
        ChunkPos pos = new ChunkPos(player.blockPosition());

        if (data.getClaims().containsKey(pos)) return false;

        String defaultName = player.getName().getString() + "'s Land";

        data.getClaims().put(pos, new Claim(player.getUUID(), defaultName));
        data.setDirty();

        return true;
    }

    public static boolean rename(ServerPlayer player, String newName) {
        ClaimData data = get(player.serverLevel());
        ChunkPos pos = new ChunkPos(player.blockPosition());

        Claim claim = data.getClaims().get(pos);

        if (claim == null) return false;
        if (!claim.getOwner().equals(player.getUUID())) return false;

        claim.setName(newName);
        data.setDirty();

        return true;
    }

    public static boolean unclaim(ServerPlayer player) {
        ClaimData data = get(player.serverLevel());
        ChunkPos pos = new ChunkPos(player.blockPosition());

        Claim claim = data.getClaims().get(pos);
        if (claim == null || !player.getUUID().equals(claim.getOwner())) return false;

        data.getClaims().remove(pos);
        data.setDirty();

        return true;
    }

    public static boolean canModify(ServerPlayer player, ChunkPos pos) {
        ClaimData data = get(player.serverLevel());

        Claim claim = data.getClaims().get(pos);
        return claim == null || claim.getOwner().equals(player.getUUID());
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("claim")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    if (claim(player)) {
                        player.sendSystemMessage(Component.literal("Chunk claimed!"));
                    } else {
                        player.sendSystemMessage(Component.literal("This chunk is already claimed!"));
                    }
                    return 1;
                })
        );

        event.getDispatcher().register(Commands.literal("unclaim")
                .executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    if (unclaim(player)) {
                        player.sendSystemMessage(Component.literal("Chunk unclaimed!"));
                    } else {
                        player.sendSystemMessage(Component.literal("You don't own this chunk!"));
                    }
                    return 1;
                })
        );

        event.getDispatcher().register(Commands.literal("claimname")
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            String name = StringArgumentType.getString(ctx, "name");

                            if (rename(player, name)) {
                                player.sendSystemMessage(Component.literal("Claim renamed!"));
                            } else {
                                player.sendSystemMessage(Component.literal("You don't own this claim!"));
                            }

                            return 1;
                        })
                )
        );
    }
}