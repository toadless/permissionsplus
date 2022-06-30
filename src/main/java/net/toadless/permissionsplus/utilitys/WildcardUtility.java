package net.toadless.permissionsplus.utilitys;

public class WildcardUtility
{
    private static final String WILDCARD_SUFFIX = ".*";
    private static final String ROOT_WILDCARD = "*";

    private WildcardUtility()
    {
        //Overrides the default, public, constructor
    }

    public static boolean isRootWildcard(String permission)
    {
        return ROOT_WILDCARD.equals(permission);
    }

    public static boolean isWildcardPermission(String permission)
    {
        return isRootWildcard(permission) || permission.endsWith(WILDCARD_SUFFIX) && permission.length() > 2; // not yet implemented
    }
}