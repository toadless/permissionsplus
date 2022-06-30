package net.toadless.permissionsplus;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.toadless.permissionsplus.utilitys.IOUtils;
import org.bukkit.Bukkit;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.InputStream;
import java.sql.Connection;
import java.util.logging.Logger;

public class DatabaseConnection
{
    private final HikariDataSource pool;
    private final PermissionsPlus permissionsPlus;

    private static final Logger logger = Bukkit.getLogger();

    public DatabaseConnection(PermissionsPlus permissionsPlus)
    {
        logger.info("Starting local database pool.");

        this.permissionsPlus = permissionsPlus;
        this.pool = initHikari();

        initTables();

        System.getProperties().setProperty("org.jooq.no-logo", "true");
    }

    private void initTables()
    {
        initTable("users");
    }

    public HikariDataSource getPool()
    {
        return pool;
    }

    public Connection getConnection()
    {
        try
        {
            return pool.getConnection();
        }
        catch (Exception exception)
        {
            return getConnection();
        }
    }

    private void initTable(String table)
    {
        try
        {
            InputStream file = IOUtils.getResourceFile("sql/" + table + ".sql");
            if (file == null)
            {
                throw new NullPointerException("File for table '" + table + "' not found");
            }
            getConnection().createStatement().execute(IOUtils.convertToString(file));
        }
        catch (Exception exception)
        {
            logger.warning("Error initializing table: '" + table + "' " + exception);
        }
    }

    private HikariDataSource initHikari()
    {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariConfig.setJdbcUrl(permissionsPlus.getConfigString("db_url"));

        hikariConfig.setUsername(permissionsPlus.getConfigString("db_username"));
        hikariConfig.setPassword(permissionsPlus.getConfigString("db_password"));

        hikariConfig.setMaximumPoolSize(30);
        hikariConfig.setMinimumIdle(10);
        hikariConfig.setConnectionTimeout(10000);
        try
        {
            return new HikariDataSource(hikariConfig);
        }
        catch (Exception exception)
        {
            logger.warning("Local database offline, connection failure.");
            System.exit(1);
            return null;
        }
    }

    public DSLContext getContext()
    {
        return getContext(getConnection());
    }

    public DSLContext getContext(Connection connection)
    {
        return DSL.using(connection, SQLDialect.POSTGRES);
    }

    public void close()
    {
        logger.info("Closed local database.");
        pool.close();
    }
}