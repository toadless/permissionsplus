package net.toadless.permissionsplus.events;

import net.toadless.permissionsplus.PermissionsPlus;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BlockBreakEvent implements Listener
{
    private final PermissionsPlus permissionsPlus;

    public BlockBreakEvent(PermissionsPlus permissionsPlus)
    {
        this.permissionsPlus = permissionsPlus;
    }

    @EventHandler
    public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent event)
    {
        if (!this.permissionsPlus.getConfig().getBoolean("building_restrictions"))
        {
            return;
        }

        Player player = event.getPlayer();

        if (!player.hasPermission("plus.breakblock")) // registered in plugin.yml!
        {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You do not have permission to do that.");
        }
    }
}