package eu.nimble.service.catalogue.util.migration;

import eu.nimble.utility.Configuration;
import eu.nimble.utility.HibernateUtility;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

/**
 * Created by suat on 26-Feb-19.
 */
public class DBConnector {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DBConnector.class);

    protected static String url;
    protected static String username;
    protected static String password;

    protected Map getConfigs() {
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        try {
            PropertySource<?> applicationYamlPropertySource = loader.load(
                    "properties", new ClassPathResource("releases/r5/r5migration.yml"), null);

            Map map = ((MapPropertySource) applicationYamlPropertySource).getSource();

            String url = (String) map.get("hibernate.connection.url");
            url = url.replace("${DB_HOST}", System.getenv("DB_HOST")).
                    replace("${DB_PORT}", System.getenv("DB_PORT")).
                    replace("${DB_DATABASE}", System.getenv("DB_DATABASE"));

            // set staging parameters
            DBConnector.url = url;
            DBConnector.username = System.getenv("DB_USERNAME");
            DBConnector.password = System.getenv("DB_PASSWORD");
            //

            map.put("hibernate.connection.url", url);
            map.put("hibernate.connection.username", DBConnector.username);
            map.put("hibernate.connection.password", DBConnector.password);

            return map;

        } catch (IOException e) {
            logger.error("", e);
            throw new RuntimeException();
        }
    }

    protected HibernateUtility getHibernateUtility() {
        HibernateUtility hu = HibernateUtility.getInstance(Configuration.UBL_PERSISTENCE_UNIT_NAME, getConfigs());
        logger.info("Retrieved hibernate utility");
        return hu;
    }

    protected void closeConnection(Connection c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
