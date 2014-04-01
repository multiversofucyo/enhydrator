package com.airhacks.enhydrator.in;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author airhacks.com
 */
public class Source {

    private String user;
    private String pwd;
    private String url;
    private String driver;
    private Connection connection;

    private Source(String driver, String url, String user, String pwd) {
        this.driver = driver;
        this.user = user;
        this.pwd = pwd;
        this.url = url;
    }

    private void connect() {
        try {
            Class.forName(this.driver);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Cannot load driver", ex);
        }
        if (this.user != null && !this.user.isEmpty()) {
            try {
                this.connection = DriverManager.getConnection(this.url, this.user, this.pwd);
            } catch (SQLException ex) {
                throw new IllegalStateException("Cannot fetch connection", ex);

            }

        } else {
            try {
                this.connection = DriverManager.getConnection(this.url);
            } catch (SQLException ex) {
                throw new IllegalStateException("Cannot fetch connection", ex);
            }
        }
    }

    public Supplier<ResultSet> query(String sql) {
        Statement stmt;
        try {
            stmt = this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            throw new IllegalStateException("Cannot prepare SQL statement", ex);
        }
        return () -> {
            try {
                return stmt.executeQuery(sql);
            } catch (SQLException ex) {
                throw new IllegalStateException("Cannot execute query: " + sql, ex);
            }
        };
    }

    public static class Configuration {

        private String url;
        private String driver;
        private String user;
        private String password;

        public Configuration driver(String driver) {
            this.driver = driver;
            return this;
        }

        public Configuration url(String url) {
            this.url = url;
            return this;
        }

        public Configuration user(String user) {
            this.user = user;
            return this;
        }

        public Configuration password(String password) {
            this.password = password;
            return this;
        }

        public Source newSource() {
            Source source = new Source(this.driver, this.url, this.user, this.password);
            source.connect();
            return source;
        }

    }

}
