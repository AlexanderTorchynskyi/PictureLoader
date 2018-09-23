package com.file.loader.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DBConfig {

    private static final String SEQUENCE_NAME = "sequence.sql";

    @Autowired
    private DataSource datasource;

    public void deleteAndCreateSequence() {
        RuntimeException ex = null;
        Connection connection = null;
        try {
            connection = datasource.getConnection();
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(SEQUENCE_NAME));
        } catch (SQLException e) {
            ex = new RuntimeException(e);
            throw ex;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    ex.getCause().addSuppressed(e);
                    throw ex;
                }
            }
        }
    }
}
