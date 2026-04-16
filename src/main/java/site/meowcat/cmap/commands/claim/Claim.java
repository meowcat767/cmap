package site.meowcat.cmap.commands.claim;

import java.util.UUID;

public class Claim {
    private UUID owner;
    private String name;

    public Claim(UUID owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
