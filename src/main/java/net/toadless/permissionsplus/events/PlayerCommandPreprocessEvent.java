package net.toadless.permissionsplus.events;

import net.toadless.permissionsplus.PermissionsPlus;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/*
 * This class is intended for disabling the op/deop command when set to in the plugin.yml
 */
public class PlayerCommandPreprocessEvent implements Listener
{
    private final PermissionsPlus permissionsPlus;

    public PlayerCommandPreprocessEvent(PermissionsPlus permissionsPlus)
    {
        this.permissionsPlus = permissionsPlus;
    }

    @EventHandler
    public void onCommand(org.bukkit.event.player.PlayerCommandPreprocessEvent event)
    {
        if (permissionsPlus.getConfig().getBoolean("allow_ops"))
        {
            return;
        }

        if (event.getMessage().contains("/op") || event.getMessage().contains("/deop"))
        {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "This command is currently disabled!");
        }
    }
}