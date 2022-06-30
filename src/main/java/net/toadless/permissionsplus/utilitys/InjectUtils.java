package net.toadless.permissionsplus.utilitys;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InjectUtils
{
    private static final String SERVER_PACKAGE_VERSION;

    private InjectUtils()
    {
        //Overrides the default, public, constructor
    }

    static
    {
        Class<?> server = Bukkit.getServer().getClass();

        Matcher matcher = Pattern.compile("^org\\.bukkit\\.craftbukkit\\.(\\w+)\\.CraftServer$").matcher(server.getName());

        if (matcher.matches())
        {
            SERVER_PACKAGE_VERSION = '.' + matcher.group(1) + '.';
        } else
        {
            SERVER_PACKAGE_VERSION = ".";
        }
    }

    public static String obtainBukkitClassPath(String className)
    {
        return "org.bukkit.craftbukkit" + SERVER_PACKAGE_VERSION + className;
    }

    public static Class<?> obtainBukkitClass(String className) throws ClassNotFoundException
    {
        return Class.forName(obtainBukkitClassPath(className));
    }

    public static void setField(Class clazz, Object instance, Object var, String varname)
    {
        try
        {
            Field f = clazz.getDeclaredField(varname);
            f.setAccessible(true);
            f.set(instance, var);
        }
        catch (Exception ex)
        {
        }
    }
}