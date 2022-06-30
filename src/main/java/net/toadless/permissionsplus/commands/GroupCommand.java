package net.toadless.permissionsplus.commands;

import net.toadless.permissionsplus.PermissionsPlus;
import net.toadless.permissionsplus.objects.Group;
import net.toadless.permissionsplus.utilitys.GroupLoader;
import net.toadless.permissionsplus.utilitys.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GroupCommand implements CommandExecutor
{
    private final PermissionsPlus permissionsPlus;

    public GroupCommand(PermissionsPlus permissionsPlus)
    {
        this.permissionsPlus = permissionsPlus;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (args.length < 1)
        {
            sender.sendMessage(ChatColor.RED + "Please provide a user!");
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if (player == null)
        {
            sender.sendMessage(ChatColor.RED +  "Please provide a valid player!");
            return true;
        }

        if (sender instanceof Player)
        {
            if (player == sender)
            {
                sender.sendMessage(ChatColor.RED + "You cannot change your own group!");
                return true;
            }
        }

        if (args.length < 2)
        {
            sender.sendMessage(ChatColor.RED + "Please provide a rank!");
            return true;
        }

        final String group = args[1];

        final GroupLoader groupLoader = this.permissionsPlus.getGroupLoader();
        final List<Group> groups = groupLoader.getAllGroups().stream().filter(g -> Objects.equals(g.name(), group)).collect(Collectors.toList());

        if (groups.isEmpty())
        {
            sender.sendMessage(ChatColor.RED + "Please provide a valid group!");
            return true;
        }

        final Group identifiedGroup = groups.get(0);

        if (sender instanceof Player)
        {
            final Group senderGroup = this.permissionsPlus.getPermissionsHandler().getPlayerGroup(((Player) sender).getPlayer()); // safe due to instanceof
            if (senderGroup.id() < identifiedGroup.id())
            {
                sender.sendMessage(ChatColor.RED + "You cannot give a player a higher group than your own!");
                return true;
            }
        }

        this.permissionsPlus.getPermissionsHandler().changePlayerGroup(player, identifiedGroup); // handles everything for us

        sender.sendMessage(ChatColor.GREEN + player.getName() + " is now " + PlayerUtils.parseColors(identifiedGroup.message()) + ChatColor.GRAY + "!");
        player.sendMessage(ChatColor.GRAY + "You are now " + PlayerUtils.parseColors(identifiedGroup.message()) + "!");

        return true;
    }
}