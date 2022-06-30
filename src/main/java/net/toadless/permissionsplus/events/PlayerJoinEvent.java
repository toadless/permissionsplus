package net.toadless.permissionsplus.events;

import net.toadless.permissionsplus.PermissionsPlus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJoinEvent implements Listener
{
    private final PermissionsPlus permissionsPlus;

    public PlayerJoinEvent(PermissionsPlus permissionsPlus)
    {
        this.permissionsPlus = permissionsPlus;
    }

    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event)
    {
        this.permissionsPlus.getPermissionsHandler().registerPlayer(event.getPlayer()); // this method will handle everything for us!
    }
}