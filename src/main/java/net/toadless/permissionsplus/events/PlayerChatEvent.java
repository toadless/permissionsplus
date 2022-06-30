package net.toadless.permissionsplus.events;

import net.toadless.permissionsplus.PermissionsPlus;
import net.toadless.permissionsplus.objects.Group;
import net.toadless.permissionsplus.utilitys.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatEvent implements Listener
{
    private final PermissionsPlus permissionsPlus;

    public PlayerChatEvent(PermissionsPlus permissionsPlus)
    {
        this.permissionsPlus = permissionsPlus;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        if (!permissionsPlus.getConfig().getBoolean("chat_formatting"))
        {
            return;
        }

        Player player = event.getPlayer();

        Group group = permissionsPlus.getPermissionsHandler().getPlayerGroup(player);

        String prefix = group.prefix();
        String chatFormat = permissionsPlus.getConfig().getString("chat_format")
                .replace("GROUP_PREFIX", prefix)
                .replace("PLAYER_NAME", player.getName())
                .replace("PLAYER_MESSAGE", event.getMessage());

        event.setFormat(PlayerUtils.parseColors(chatFormat));
    }
}