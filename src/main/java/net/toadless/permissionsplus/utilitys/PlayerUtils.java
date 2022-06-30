package net.toadless.permissionsplus.utilitys;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.toadless.permissionsplus.objects.Group;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerUtils
{
    private PlayerUtils()
    {
        //Overrides the default, public, constructor
    }

    public static String parseColors(String text)
    {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void setTab(Player player, Group group)
    {
        player.playerListName(Component.text(parseColors(group.tab().replace("PLAYER_NAME", player.getName()))));
    }

    public static void setTag(Player player, Group group)
    {
        player.customName(Component.text(parseColors(group.tag().replace("PLAYER_NAME", player.getName()))));
    }
}