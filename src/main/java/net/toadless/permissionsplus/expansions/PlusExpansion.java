package net.toadless.permissionsplus.expansions;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.toadless.permissionsplus.PermissionsPlus;
import net.toadless.permissionsplus.objects.Group;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlusExpansion extends PlaceholderExpansion
{
    private PermissionsPlus permissionsPlus;

    public PlusExpansion(PermissionsPlus permissionsPlus)
    {
        this.permissionsPlus = permissionsPlus;
    }

    @Override
    public @NotNull String getIdentifier()
    {
        return "plus";
    }

    @Override
    public @NotNull String getAuthor()
    {
        return "toadless";
    }

    @Override
    public @NotNull String getVersion()
    {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier)
    {
        if (identifier.equalsIgnoreCase("PLAYER_GROUP"))
        {
            Group group = this.permissionsPlus.getPermissionsHandler().getPlayerGroup(player);
            return group.name();
        }

        if (identifier.equalsIgnoreCase("PLAYER_GROUP_PREFIX"))
        {
            Group group = this.permissionsPlus.getPermissionsHandler().getPlayerGroup(player);
            return group.prefix();
        }

        if (identifier.equalsIgnoreCase("PLAYER_GROUP_TAG"))
        {
            Group group = this.permissionsPlus.getPermissionsHandler().getPlayerGroup(player);
            return group.tag();
        }

        if (identifier.equalsIgnoreCase("PLAYER_GROUP_TAB"))
        {
            Group group = this.permissionsPlus.getPermissionsHandler().getPlayerGroup(player);
            return group.tab();
        }

        if (identifier.equalsIgnoreCase("PLAYER_GROUP_MESSAGE"))
        {
            Group group = this.permissionsPlus.getPermissionsHandler().getPlayerGroup(player);
            return group.message();
        }

        return null;
    }
}