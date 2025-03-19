package com.example.guiex1.repository.dbrepo;

import com.example.guiex1.domain.Prietenie;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.domain.Validator;
import com.example.guiex1.repository.Repository;
import com.example.guiex1.utils.paging.Page;
import com.example.guiex1.utils.paging.Pageable;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class FriendshipDbRepository implements Repository<Tuple<Long,Long>, Prietenie> {
    private String url;
    private String username;
    private String password;
    private Validator<Prietenie> validator;

    public FriendshipDbRepository(String url, String username, String password, Validator<Prietenie> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    /**
     * @param id - long, the id of a user to found
     * @return Optional<User> - the user with the given id
     * -Optional.empty() otherwise
     */
    @Override
    public Optional<Prietenie> findOne(Tuple<Long, Long> id) {
        Prietenie friendship;
        Long id1 = id.getLeft();
        Long id2 = id.getRight();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             ResultSet resultSet = connection.createStatement().executeQuery(String.format("select * from friend where id1 = '%d' and id2 = '%d' or id1='%d' and id2='%d'", id1, id2, id2, id1))) {
            if (resultSet.next()) {
                friendship = createFriendFromResultSet(resultSet);
                return Optional.ofNullable(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    private Prietenie createFriendFromResultSet(ResultSet resultSet) {
        try {
            Long id1 = resultSet.getLong("id1");
            Long id2 = resultSet.getLong("id2");
            Timestamp date = resultSet.getTimestamp("f_date");
            String status = resultSet.getString("status");
            LocalDateTime localDateTime = date.toLocalDateTime();
            Prietenie friendship = new Prietenie(id1, id2, localDateTime, status);
            return friendship;
        } catch (SQLException e) {
            return null;
        }
    }


    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friend");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Prietenie prietenie = createFriendFromResultSet(resultSet);
                friendships.add(prietenie);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Optional<Prietenie> save(Prietenie entity) {
        String sql = "insert into friend (id1, id2,f_date,status) values (?, ?,?,?)";
        validator.validate(entity);
        if (findOne(entity.getId()).isPresent())
            return Optional.ofNullable(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getId().getLeft());
            ps.setLong(2, entity.getId().getRight());
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(entity.getDate()));
            ps.setString(4, entity.getStatus());

            ps.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
            return Optional.ofNullable(entity);
        }
        return Optional.empty();
    }


    @Override
    public Optional<Prietenie> delete(Tuple<Long, Long> id) {
        Long id1 = id.getLeft();
        Long id2 = id.getRight();
        String sql = "delete from friend where id1 = ? and id2 = ? or id1 = ? and id2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            Optional<Prietenie> friendship = findOne(id);
            if (!friendship.isEmpty()) {
                ps.setLong(1, id1);
                ps.setLong(2, id2);
                ps.setLong(3, id2);
                ps.setLong(4, id1);
                ps.executeUpdate();
            }
            return friendship;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Prietenie> update(Prietenie friendship) {
        if (friendship == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(friendship);
        Long id1 = friendship.getId().getLeft();
        Long id2 = friendship.getId().getRight();
        String status = friendship.getStatus();
        String sql = "update friend set status = ? where id1 = ? and id2 = ? or id1 = ? and id2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, id1);
            ps.setLong(3, id2);
            ps.setLong(4, id2);
            ps.setLong(5, id1);
            if (ps.executeUpdate() > 0)
                return Optional.empty();
            return Optional.ofNullable(friendship);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private int count(Connection connection, Long id) throws SQLException {
        String sql = "select count(*) as count from friend where id1 = ? or id2 = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, id);
            try (ResultSet result = statement.executeQuery()) {
                int totalNumberOfFriends = 0;
                if (result.next()) {
                    totalNumberOfFriends = result.getInt("count");
                }
                return totalNumberOfFriends;
            }
        }
    }

    public Page<Prietenie> findAllOnPage(Long id, Pageable pageable) {
        List<Prietenie> friendsonPage = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friend where (id1 = ? or id2 = ?) and status = 'active' limit ? offset ?")) {
            statement.setLong(1, id);
            statement.setLong(2, id);
            statement.setInt(3, pageable.getPageSize());
            statement.setInt(4,pageable.getPageNumber() * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Prietenie prietenie = createFriendFromResultSet(resultSet);
                friendsonPage.add(prietenie);
            }
            return new Page<Prietenie> (friendsonPage, count(connection, id));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
