package net.toadless.permissionsplus.inject;

import net.toadless.permissionsplus.utilitys.InjectUtils;
import net.toadless.permissionsplus.utilitys.WildcardUtility;
import org.bukkit.entity.Player;
import org.bukkit.permissions.*;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class PermissionsPlusPermissible extends PermissibleBase
{
    private final Player player;
    private final Map<String, PermissionAttachmentInfo> permissions;

    private Permissible oldPermissible;

    public PermissionsPlusPermissible(Player player)
    {
        super(player);

        this.player = player;
        this.permissions = new LinkedHashMap<>();
        this.oldPermissible = new PermissibleBase(null);

        InjectUtils.setField(PermissibleBase.class, this, permissions, "permissions");
    }

    public Permissible getOldPermissible()
    {
        return this.oldPermissible;
    }

    public void setOldPermissible(Permissible oldPermissible)
    {
        this.oldPermissible = oldPermissible;
    }

    @Override
    public boolean hasPermission(@NotNull String permission)
    {
        Set<PermissionAttachmentInfo> permissions = player.getEffectivePermissions();

        for (PermissionAttachmentInfo perm : permissions)
        {
            if (!WildcardUtility.isRootWildcard(perm.getPermission())) continue;
            else return true;
        }

        return super.hasPermission(permission);
    }

    @Override
    public boolean hasPermission(@NotNull Permission permission)
    {
        return hasPermission(permission.getName());
    }
}