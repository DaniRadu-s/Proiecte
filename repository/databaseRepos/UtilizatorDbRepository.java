package com.example.guiex1.repository.dbrepo;

import com.example.guiex1.domain.Prietenie;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.domain.Validator;
import com.example.guiex1.repository.Repository;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.guiex1.utils.Hashing.hashPassword;

public class UtilizatorDbRepository implements Repository<Long, Utilizator> {
    private String url;
    private String username;
    private String password;
    private Validator<Utilizator> validator;

    public UtilizatorDbRepository(String url, String username, String password, Validator<Utilizator> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    /**
     * @param id - long, the id of a user to found
     * @return Optional<User> - the user with the given id
     *                        -Optional.empty() otherwise
     */
    @Override
    public Optional<Utilizator> findOne(Long id) {
        Utilizator user;
        try(Connection connection = DriverManager.getConnection(url, username, password);
            ResultSet resultSet = connection.createStatement().executeQuery(String.format("select * from users U where U.id = '%d'", id))) {
            if(resultSet.next()){
                user = createUserFromResultSet(resultSet);
                List<Tuple<Utilizator,String>> friends = new ArrayList<>();
                PreparedStatement statementF = connection.prepareStatement(
                        "select id,first_name,last_name,status from friend f\n" +
                                "join users u on u.id != ? and (u.id = f.id1 or u.id = f.id2)\n" +
                                "where id1 = ? or id2 = ?"
                );
                statementF.setLong(1, id);
                statementF.setLong(2, id);
                statementF.setLong(3, id);
                ResultSet resultSetF = statementF.executeQuery();
                while (resultSetF.next()) {
                    Utilizator utilizator = createUserFromResultSet(resultSetF);
                    String status = resultSetF.getString("status");
                    Tuple<Utilizator,String> friend = new Tuple<>(utilizator, status);
                    friends.add(friend);
                }

                user.setFriends(friends);
                return Optional.ofNullable(user);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    private Utilizator createUserFromResultSet(ResultSet resultSet) {
        try {
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");

            Long idd = resultSet.getLong("id");
            Utilizator user = new Utilizator(firstName, lastName);
            user.setId(idd);
            return user;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Utilizator user = createUserFromResultSet(resultSet);
                Long id = user.getId();
                List<Tuple<Utilizator,String>> friends = new ArrayList<>();
                PreparedStatement statementF = connection.prepareStatement(
                        "select id,first_name,last_name,status from friend f\n" +
                                "join users u on u.id != ? and (u.id = f.id1 or u.id = f.id2)\n" +
                                "where id1 = ? or id2 = ?"
                );
                statementF.setLong(1, id);
                statementF.setLong(2, id);
                statementF.setLong(3, id);
                ResultSet resultSetF = statementF.executeQuery();
                while (resultSetF.next()) {
                    Utilizator utilizator = createUserFromResultSet(resultSetF);
                    String status = resultSetF.getString("status");
                    Tuple<Utilizator,String> friend = new Tuple<>(utilizator, status);
                    friends.add(friend);
                }
                user.setFriends(friends);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        String sql = "insert into users (first_name, last_name) values (?, ?)";  // Inserare utilizator

        validator.validate(entity);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            // Inserare utilizator în tabela `users`
            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.executeUpdate();

            return Optional.of(entity);  // Returnează utilizatorul cu ID-ul setat

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void setParola(Utilizator user, String s){
        String sql1 = "insert into passwords (userID, HashedPassword) values (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql1)) {

            // Inserare utilizator în tabela `users`
            ps.setLong(1, user.getId());
            String pass = hashPassword(s);
            ps.setString(2, pass);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> delete(Long id) {
        String sql = "delete from users where id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            Optional<Utilizator> user = findOne(id);
            if(!user.isEmpty()) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Utilizator> updatePassword(Utilizator user, String s){
        if (user == null) {
            throw new IllegalArgumentException("Entity must not be null!");
        }

        // Validează obiectul utilizator
        validator.validate(user);

        // Interogare pentru a actualiza parola în tabelul 'passwords'
        String sql = "UPDATE passwords SET HashedPassword = ? " +
                "WHERE userID = (SELECT id FROM users WHERE first_name = ? AND last_name = ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            // Setează parametrii pentru actualizarea parolei
            ps.setString(1, s);  // Parola hashuită
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());

            // Execută interogarea și verifică dacă a fost afectat un rând
            if (ps.executeUpdate() > 0) {
                return Optional.of(user); // Parola a fost actualizată
            }

            // Dacă nu a fost afectat niciun rând (utilizator inexistent)
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace(); // Debugging
            throw new RuntimeException("Eroare la actualizarea parolei: " + e.getMessage());
        }
    }


    @Override
    public Optional<Utilizator> update(Utilizator user) {
        if(user == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(user);
        String sql = "update users set first_name = ?, last_name = ? where id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1,user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setLong(3, user.getId());
            if( ps.executeUpdate() > 0 )
                return Optional.empty();
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
