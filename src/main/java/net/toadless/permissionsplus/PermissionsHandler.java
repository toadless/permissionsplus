package net.toadless.permissionsplus;

import net.toadless.permissionsplus.inject.PermissibleInjector;
import net.toadless.permissionsplus.inject.PermissionsPlusPermissible;
import net.toadless.permissionsplus.jooq.tables.pojos.Users;
import net.toadless.permissionsplus.objects.Group;
import net.toadless.permissionsplus.objects.Permission;
import net.toadless.permissionsplus.utilitys.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import static net.toadless.permissionsplus.jooq.Tables.USERS;

public class PermissionsHandler
{
    private final PermissionsPlus permissionsPlus;

    private final HashMap<UUID, PermissionAttachment> playerPermissions;
    private final HashMap<UUID, Group> playerGroups;

    private final Logger logger = Bukkit.getLogger();

    public PermissionsHandler(PermissionsPlus permissionsPlus)
    {
        this.permissionsPlus = permissionsPlus;
        this.playerPermissions = new HashMap<>();
        this.playerGroups = new HashMap<>();
    }

    public void changePlayerGroup(Player player, Group newGroup)
    {
        final UUID uuid = player.getUniqueId();

        this.playerGroups.put(uuid, newGroup);
        updatePlayerInDatabase(new Users(String.valueOf(uuid), (long) newGroup.id()));

        unregisterPlayer(player); // remove all players previous attachments

        // assign player group to stop extra database call
        this.playerGroups.put(uuid, newGroup);

        registerPlayer(player); // register player again with new values
    }

    public Group getPlayerGroup(Player player)
    {
        final UUID uuid = player.getUniqueId();

        if (this.playerGroups.containsKey(uuid))
        {
            return this.playerGroups.get(uuid);
        }

        final Users user = fetchPlayerFromDatabase(uuid);
        if (user == null) return null;
        return this.permissionsPlus.getGroupLoader().getGroupById(user.getGroupId());
    }

    public void registerPlayer(Player player)
    {
        if (!PermissibleInjector.checkInjected(player))
        {
            PermissionsPlusPermissible permissionsPlusPermissible = new PermissionsPlusPermissible(player);

            try
            {
                PermissibleInjector.inject(player, permissionsPlusPermissible);
            } catch (IllegalAccessException exception)
            {
                throw new RuntimeException(exception); // shouldn't occur
            }
        }

        final UUID uuid = player.getUniqueId();

        Group group = getPlayerGroup(player);

        // if new player
        if (group == null)
        {
            final long defaultGroupId = this.permissionsPlus.getGroupLoader().getDefaultGroupId();

            Users user = new Users(
                    String.valueOf(player.getUniqueId()),
                    defaultGroupId
            );

            insertPlayerIntoDatabase(user);

            group = this.permissionsPlus.getGroupLoader().getGroupById(defaultGroupId);
        }

        this.playerGroups.put(uuid, group);

        // set player metadata
        if (this.permissionsPlus.getConfig().getBoolean("tablist"))
        {
            PlayerUtils.setTab(player, group);
        }

        if (this.permissionsPlus.getConfig().getBoolean("tags"))
        {
            PlayerUtils.setTag(player, group);
        }

        // create new permission attachment for player
        PermissionAttachment attachment = player.addAttachment(permissionsPlus);
        this.playerPermissions.put(uuid, attachment);

        // add positive permissions
        for (Permission permission : group.permissions())
        {
            if (!permission.positive())
            {
                continue;
            }

            attachment.setPermission(permission.permission(), true);
        }

        // add negative permissions
        for (Permission permission : group.permissions())
        {
            if (permission.positive())
            {
                continue;
            }

            attachment.unsetPermission(permission.permission());
        }
    }

    public void unregisterPlayer(Player player)
    {
        final UUID uuid = player.getUniqueId();

        PermissionAttachment attachment = this.playerPermissions.get(uuid);
        player.removeAttachment(attachment);

        if (PermissibleInjector.checkInjected(player))
        {
            try
            {
                PermissibleInjector.uninject(player);
            } catch (IllegalAccessException e)
            {
                throw new RuntimeException(e); // shouldn't occur
            }
        }

        this.playerPermissions.remove(uuid);
        this.playerGroups.remove(uuid);
    }

    public void shutdownPermissionsHandler()
    {
        this.playerPermissions.forEach((uuid, permissionAttachment) -> Objects.requireNonNull(this.permissionsPlus.getServer().getPlayer(uuid)).removeAttachment(permissionAttachment));

        // don't think this will get used, just encase ill add it anyway
        for (Player player : this.permissionsPlus.getServer().getOnlinePlayers())
        {
            if (PermissibleInjector.checkInjected(player))
            {
                try
                {
                    PermissibleInjector.uninject(player);
                } catch (IllegalAccessException e)
                {
                    throw new RuntimeException(e); // shouldn't occur
                }
            }
        }

        this.playerPermissions.clear();
        this.playerGroups.clear();
    }

    //
    // Database Methods
    //

    private Users fetchPlayerFromDatabase(UUID uuid)
    {
        try (Connection connection = this.permissionsPlus.getDatabaseConnection().getConnection())
        {
            var context = this.permissionsPlus.getDatabaseConnection().getContext(connection);
            var query = context.selectFrom(USERS).where(USERS.UUID.eq(String.valueOf(uuid)));
            var result = query.fetchOne();

            if (query.fetch().isEmpty())
            {
                return null;
            }

            return new Users(result.getUuid(), result.getGroupId()); // save due to previous if statement
        } catch (SQLException exception)
        {
            this.logger.warning("Failed to fetch player " + uuid + " from the database " + exception);
            return null;
        }
    }

    private void updatePlayerInDatabase(Users user)
    {
        try (Connection connection = this.permissionsPlus.getDatabaseConnection().getConnection())
        {
            var context = this.permissionsPlus.getDatabaseConnection().getContext(connection);
            context.update(USERS).set(USERS.GROUP_ID, user.getGroupId()).where(USERS.UUID.eq(user.getUuid())).execute();
        } catch (SQLException exception)
        {
            this.logger.warning("Failed to update player data in the database for " + user.getUuid() + " " + exception);
        }
    }

    private void insertPlayerIntoDatabase(Users user)
    {
        try (Connection connection = this.permissionsPlus.getDatabaseConnection().getConnection())
        {
            var context = this.permissionsPlus.getDatabaseConnection().getContext(connection)
                    .insertInto(USERS)
                    .columns(USERS.UUID, USERS.GROUP_ID)
                    .values(user.getUuid(), user.getGroupId())
                    .onDuplicateKeyIgnore();
            context.execute();
        } catch (SQLException exception)
        {
            this.logger.warning("Failed to insert player into the database for " + user.getUuid() + " " + exception);
        }
    }
}