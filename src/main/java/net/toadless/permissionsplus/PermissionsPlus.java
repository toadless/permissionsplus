package net.toadless.permissionsplus;

import net.toadless.permissionsplus.commands.GroupCommand;
import net.toadless.permissionsplus.events.*;
import net.toadless.permissionsplus.expansions.PlusExpansion;
import net.toadless.permissionsplus.utilitys.GroupLoader;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public final class PermissionsPlus extends JavaPlugin
{
    private Map<CommandExecutor, String> commands = Map.of(
            new GroupCommand(this), "group"
    );

    private List<Listener> events = List.of(
            new PlayerJoinEvent(this),
            new PlayerLeaveEvent(this),
            new PlayerChatEvent(this),
            new PlayerCommandPreprocessEvent(this),
            new BlockPlaceEvent(this),
            new BlockBreakEvent(this)
    );

    private GroupLoader groupLoader;
    private DatabaseConnection databaseConnection;
    private PermissionsHandler permissionsHandler;

    @Override
    public void onEnable()
    {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.groupLoader = new GroupLoader(this);
        this.databaseConnection = new DatabaseConnection(this);
        this.permissionsHandler = new PermissionsHandler(this);

        if (!this.getConfig().getBoolean("allow_ops"))
        {
            for (OfflinePlayer player : this.getServer().getOperators())
            {
                player.setOp(false);
            }
        }

        loadCommands();
        loadEvents();

        // setup permissions for every online player if a reload happens
        for (Player player : this.getServer().getOnlinePlayers())
        {
            this.getPermissionsHandler().registerPlayer(player);
        }

        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null)
        {
            new PlusExpansion(this).register();
        }
    }

    @Override
    public void onDisable()
    {
        this.databaseConnection.close();
        this.permissionsHandler.shutdownPermissionsHandler(); // this will clear and uninject everything
    }

    private void loadEvents()
    {
        for (Listener event : events)
        {
            this.getServer().getPluginManager().registerEvents(event, this);
        }
    }

    private void loadCommands()
    {
        this.commands.forEach((commandExecutor, s) -> getCommand(s).setExecutor(commandExecutor)); // safe because its hardcoded
    }

    public String getConfigString(String key)
    {
        return this.getConfig().getString(key);
    }

    public GroupLoader getGroupLoader()
    {
        return this.groupLoader;
    }

    public DatabaseConnection getDatabaseConnection()
    {
        return this.databaseConnection;
    }

    public PermissionsHandler getPermissionsHandler()
    {
        return this.permissionsHandler;
    }
}
