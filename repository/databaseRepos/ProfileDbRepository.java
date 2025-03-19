package com.example.guiex1.repository.dbrepo;

import com.example.guiex1.domain.Profile;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfileDbRepository implements Repository<Long, Profile> {

    private String url;
    private String username;
    private String password;

    public ProfileDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    @Override
    public Optional<Profile> findOne(Long id) {
        Profile profilePage;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM profile WHERE Uid = ?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                profilePage= createProfilePageFromResultSet(resultSet);
                return Optional.ofNullable(profilePage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    private Profile createProfilePageFromResultSet(ResultSet resultSet) {
        try {
            String image_url = resultSet.getString("imageURL");
            String description = resultSet.getString("description");
            Long UID= resultSet.getLong("Uid");
            Profile profilePage = new Profile(image_url,description,UID);
            return profilePage;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Iterable<Profile> findAll() {
        return null;
    }

    @Override
    public Optional<Profile> save(Profile entity) {
        String sql = "insert into profile (Uid, imageURL, description) values (?, ?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getUid());
            ps.setString(2, entity.getImageURL());
            ps.setString(3, entity.getDescription());

            ps.executeUpdate();
        } catch (SQLException e) {
            //e.printStackTrace();
            return Optional.ofNullable(entity);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Profile> delete(Long aLong) {
        String sql = "delete from profile where Uid = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            Optional<Profile> pr = findOne(aLong);
            if(!pr.isEmpty()) {
                ps.setLong(1, pr.get().getUid());
                ps.executeUpdate();
            }
            return pr;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Profile> update(Profile entity) {
        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        String sql = "update profile set imageURL = ?,description = ? where Uid = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getImageURL());
            ps.setString(2, entity.getDescription());
            ps.setLong(3,entity.getUid());
            Long id = entity.getUid();
            String i = entity.getImageURL();
            String S = entity.getDescription();
            if( ps.executeUpdate() > 0 )
                return Optional.empty();
            return Optional.ofNullable(entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Profile> findProfile(Long userId){
        Profile profilePage;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM profile WHERE Uid = ?")) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                profilePage= createProfilePageFromResultSet(resultSet);
                return Optional.ofNullable(profilePage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
