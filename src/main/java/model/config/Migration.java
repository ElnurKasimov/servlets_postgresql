package model.config;

import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.SQLException;

public class Migration {
    private DatabaseManagerConnector manager;

    public Migration (DatabaseManagerConnector manager) {
        this.manager = manager;
    }

    public void initDb() {
        try {
            Connection connection = manager.getConnection();
             Flyway flyway = Flyway
                    .configure()
                    .baselineOnMigrate(true)
                     .dataSource(manager.getUrl(), System.getenv("dbusername"), System.getenv("dbPassword"))
                    .load();
            flyway.migrate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
    }
}
