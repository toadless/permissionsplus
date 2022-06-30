package net.toadless.permissionsplus.objects;

import java.util.List;

public record Group(
        int id,
        String name,
        boolean isDefault,
        String prefix,
        String tag,
        String tab,
        String message,
        List<Permission> permissions)
{
}