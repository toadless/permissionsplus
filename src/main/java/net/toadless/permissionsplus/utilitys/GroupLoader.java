package net.toadless.permissionsplus.utilitys;

import net.toadless.permissionsplus.PermissionsPlus;
import net.toadless.permissionsplus.objects.Group;
import net.toadless.permissionsplus.objects.Permission;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GroupLoader
{
    private final PermissionsPlus permissionsPlus;
    private final List<Group> groups;

    private int defaultGroupId;

    public GroupLoader(PermissionsPlus permissionsPlus)
    {
        this.permissionsPlus = permissionsPlus;
        this.groups = loadAllGroups();
    }

    public Group getGroupById(long id)
    {
        return getAllGroups().stream().filter(group -> group.id() == id).collect(Collectors.toList()).get(0);
    }

    public Group getGroupByName(String name)
    {
        return getAllGroups().stream().filter(group -> Objects.equals(group.name(), name)).collect(Collectors.toList()).get(0);
    }

    public List<Group> getAllGroups()
    {
        return this.groups;
    }

    // load all groups in yaml config file
    private List<Group> loadAllGroups()
    {
        List<Group> groups = new LinkedList<>();

        for (String group: permissionsPlus.getConfig().getConfigurationSection("groups").getKeys(false))
        {
            final int id = Integer.parseInt(getGroupStringValue(group, "id"));
            final boolean isDefault = getGroupBooleanValue(group, "default");
            final String prefix = getGroupStringValue(group, "prefix");
            final String tag = getGroupStringValue(group, "tag");
            final String tab = getGroupStringValue(group, "tab");
            final String message = getGroupStringValue(group, "message");
            final List<Permission> permissions = getGroupPermissions(group);

            groups.add(
                    new Group(
                            id,
                            group,
                            isDefault,
                            prefix,
                            tag,
                            tab,
                            message,
                            permissions
                    )
            );

            if (isDefault)
            {
                defaultGroupId = id;
            }
        }

        return groups;
    }

    private String getGroupStringValue(String group, String value)
    {
        return permissionsPlus.getConfig().getString("groups." + group + "." + value);
    }

    private Boolean getGroupBooleanValue(String group, String value)
    {
        return permissionsPlus.getConfig().getBoolean("groups." + group + "." + value);
    }

    private List<Permission> getGroupPermissions(String group)
    {
        List<String> allowedPermissions = permissionsPlus.getConfig().getStringList("groups." + group + ".allowedPermissions");
        List<String> disallowedPermissions = permissionsPlus.getConfig().getStringList("groups." + group + ".disallowedPermissions");

        List<Permission> permissions = new LinkedList<>();

        for (String allowedPermission : allowedPermissions)
        {
            Permission permission = new Permission(allowedPermission, true);
            permissions.add(permission);
        }

        for (String disallowedPermission : disallowedPermissions)
        {
            Permission permission = new Permission(disallowedPermission, false);
            permissions.add(permission);
        }

        return permissions;
    }

    public int getDefaultGroupId()
    {
        return defaultGroupId;
    }
}