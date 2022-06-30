package net.toadless.permissionsplus.events;

import net.toadless.permissionsplus.PermissionsPlus;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BlockPlaceEvent implements Listener
{
    private final PermissionsPlus permissionsPlus;

    public BlockPlaceEvent(PermissionsPlus permissionsPlus)
    {
        this.permissionsPlus = permissionsPlus;
    }

    @EventHandler
    public void onBlockPlace(org.bukkit.event.block.BlockPlaceEvent event)
    {
        if (!this.permissionsPlus.getConfig().getBoolean("building_restrictions"))
        {
            return;
        }

        Player player = event.getPlayer();

        if (!player.hasPermission("plus.placeblock")) // registered in plugin.yml!
        {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You do not have permission to do that.");
        }
    }
}