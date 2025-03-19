package com.example.guiex1;

import com.example.guiex1.controller.LoginController;
//import com.example.guiex1.controller.UtilizatorController;
import com.example.guiex1.domain.*;
import com.example.guiex1.repository.Repository;
import com.example.guiex1.repository.dbrepo.FriendshipDbRepository;
import com.example.guiex1.repository.dbrepo.MessageDbRepository;
import com.example.guiex1.repository.dbrepo.ProfileDbRepository;
import com.example.guiex1.repository.dbrepo.UtilizatorDbRepository;
import com.example.guiex1.services.UtilizatorService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;


public class HelloApplication extends Application {

    Repository<Long, Utilizator> utilizatorRepository;
    UtilizatorService service;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        System.out.println("Reading data from file");
        String username="------";
        String pasword="------";
        String url="jdbc:postgresql://localhost:5432/------";
        UtilizatorDbRepository utilizatorRepository =
                new UtilizatorDbRepository(url,username, pasword,  new UtilizatorValidator());
        FriendshipDbRepository repofriend = new FriendshipDbRepository(url, username,pasword, new FriendshipValidator());
        MessageDbRepository repoMessage = new MessageDbRepository(url,username,pasword);
        ProfileDbRepository repoProfile = new ProfileDbRepository(url,username,pasword);
        service =new UtilizatorService(utilizatorRepository, repofriend, repoMessage, repoProfile);
        initView(primaryStage);
        primaryStage.setWidth(800);
        primaryStage.show();


    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/login-view.fxml"));

        AnchorPane userLayout = fxmlLoader.load();
        primaryStage.setScene(new Scene(userLayout));

        LoginController userController = fxmlLoader.getController();
        userController.setService(service, primaryStage);

    }
}