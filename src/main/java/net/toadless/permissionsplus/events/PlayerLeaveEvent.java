package net.toadless.permissionsplus.events;

import net.toadless.permissionsplus.PermissionsPlus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveEvent implements Listener
{
    private final PermissionsPlus permissionsPlus;

    public PlayerLeaveEvent(PermissionsPlus permissionsPlus)
    {
        this.permissionsPlus = permissionsPlus;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        permissionsPlus.getPermissionsHandler().unregisterPlayer(event.getPlayer()); // this method will handle everything for us!
    }
}