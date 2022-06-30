package net.toadless.permissionsplus.inject;

import net.toadless.permissionsplus.utilitys.InjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;

import java.lang.reflect.Field;
import java.util.logging.Logger;

public class PermissibleInjector
{
    private static final Logger logger = Bukkit.getLogger();

    private static final Field HUMAN_ENTITY_PERMISSIBLE_FIELD;
    private static final Field PERMISSIBLE_BASE_ATTACHMENTS_FIELD;

    static
    {
        try
        {
            Field humanEntityPermissibleField;

            try
            {
                humanEntityPermissibleField = InjectUtils.obtainBukkitClass("entity.CraftHumanEntity").getDeclaredField("perm");
                humanEntityPermissibleField.setAccessible(true);
            } catch (Exception exception)
            {
                humanEntityPermissibleField = Class.forName("net.glowstone.entity.GlowHumanEntity").getDeclaredField("permissions");
                humanEntityPermissibleField.setAccessible(true);
            }

            HUMAN_ENTITY_PERMISSIBLE_FIELD = humanEntityPermissibleField;

            PERMISSIBLE_BASE_ATTACHMENTS_FIELD = PermissibleBase.class.getDeclaredField("attachments");
            PERMISSIBLE_BASE_ATTACHMENTS_FIELD.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException exception)
        {
            throw new ExceptionInInitializerError(exception);
        }
    }

    public static void inject(Player player, PermissionsPlusPermissible newPermissible) throws IllegalAccessException
    {
        PermissibleBase oldPermissible = (PermissibleBase) HUMAN_ENTITY_PERMISSIBLE_FIELD.get(player);

        if (oldPermissible instanceof PermissionsPlusPermissible)
        {
            throw new IllegalStateException("Permissions Plus Permissible already injected into player" + player.getName());
        }

        Class<? extends PermissibleBase> oldClass = oldPermissible.getClass();

        if (!PermissibleBase.class.equals(oldClass))
        {
            logger.warning("Player " + player.getName() + " already has a custom permissible!\n" +
                    "Please make sure that PermissionsPlus is the only permissions plugin that you have installed.");
        }

        newPermissible.clearPermissions();
        newPermissible.setOldPermissible(oldPermissible);

        HUMAN_ENTITY_PERMISSIBLE_FIELD.set(player, newPermissible);
    }

    public static void uninject(Player player) throws IllegalAccessException
    {
        PermissibleBase permissible = (PermissibleBase) HUMAN_ENTITY_PERMISSIBLE_FIELD.get(player);

        if (permissible instanceof PermissionsPlusPermissible)
        {
            HUMAN_ENTITY_PERMISSIBLE_FIELD.set(player, ((PermissionsPlusPermissible) permissible).getOldPermissible());
        }
    }

    public static boolean checkInjected(Player player)
    {
        PermissibleBase permissibleBase;

        try
        {
            permissibleBase = (PermissibleBase) HUMAN_ENTITY_PERMISSIBLE_FIELD.get(player);
        } catch(IllegalAccessException exception)
        {
            return false; // shouldn't occur
        }

        if (permissibleBase instanceof PermissionsPlusPermissible)
        {
            return true;
        }

        if (!(permissibleBase instanceof Permissible))
        {
            logger.warning("Player " + player.getName() + " already has a custom permissible!\n" +
                    "Please make sure that PermissionsPlus is the only permissions plugin that you have installed.");
        }

        return false;
    }
}