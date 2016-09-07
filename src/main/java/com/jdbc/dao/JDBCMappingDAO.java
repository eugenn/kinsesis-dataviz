package com.jdbc.dao;

import com.jdbc.vo.Mapping;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class JDBCMappingDAO implements MappingDAO {

    Connection connection = null;
    private static final BasicDataSource dataSource = new BasicDataSource();

    public Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            if (connection == null)
                connection = DriverManager.getConnection("jdbc:mysql://localhost/kinesis?user=root&password=root&autoReconnect=true&useSSL=false");

        } catch (ClassNotFoundException | SQLException e) {

            e.printStackTrace();

        }
        return connection;
    }

    @Override
    public void insert(Mapping mapping) {
        try (PreparedStatement preparedStatement =
                     getConnection().prepareStatement("INSERT INTO kinesis.mapping (bidrequestId ,banneridId, audience, timestamp) VALUES (? , ?, ?, ?)")) {
            preparedStatement.setString(1, mapping.getBidrequestId());
            preparedStatement.setString(2, mapping.getBannerId());
            preparedStatement.setString(3, mapping.getAudienceId());
            preparedStatement.setTimestamp(4, mapping.getTimestamp());
            preparedStatement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public Mapping load(String bidrequestId) {
        try (Statement statement = getConnection().createStatement()) {

            ResultSet resultSet = statement.executeQuery("select * from mapping where bidrequestId=\"" + bidrequestId + "\"");
            if (resultSet.next()) {
                return new Mapping(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getTimestamp(4));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void batchInsert(List<Mapping> mappings) {
        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO kinesis.mapping (bidrequestId, bannerId, audienceId, timestamp) VALUES (? , ?, ?, ?)")) {
            int i = 0;

            for (Mapping mapping : mappings) {
                preparedStatement.setString(1, mapping.getBidrequestId());
                preparedStatement.setString(2, mapping.getBannerId());
                preparedStatement.setString(3, mapping.getAudienceId());
                preparedStatement.setTimestamp(4, mapping.getTimestamp());
                preparedStatement.addBatch();

                i++;
                if (i % 1000 == 0 || i == mappings.size()) {
                    preparedStatement.executeBatch(); // Execute every 1000 items.
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteAll() {
        String deleteSQL = "DELETE FROM mapping WHERE mapping.timestamp < ADDDATE(NOW(), INTERVAL -1 HOUR)";

        try (Connection conn = getConnection();
             Statement statement = conn.createStatement()) {
            statement.execute(deleteSQL);


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Mapping> select() {
        List<Mapping> mappings = new LinkedList<>();

        try (Connection conn = getConnection()) {
            try (Statement statement = conn.createStatement()) {
                ResultSet resultSet = statement.executeQuery("SELECT * FROM kinesis.mapping");

                while (resultSet.next()) {
                    mappings.add(new Mapping(resultSet.getString("bidrequestId"), resultSet.getString("banneridId"),
                            resultSet.getString("audience"), resultSet.getTimestamp("timestamp")));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mappings;
    }

    public long count() {
        try (Connection conn = getConnection();
             Statement statement = conn.createStatement();
             ResultSet set = statement.executeQuery("SELECT COUNT(1) FROM mapping")) {
            if (set.next()) {
                return set.getLong(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0l;
    }


}
