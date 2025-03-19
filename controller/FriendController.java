package com.example.guiex1.controller;

import com.example.guiex1.domain.Prietenie;
import com.example.guiex1.domain.Profile;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.services.UtilizatorService;
import com.example.guiex1.utils.events.Event;
import com.example.guiex1.utils.events.FriendshipEntityChangeEvent;
import com.example.guiex1.utils.events.UtilizatorEntityChangeEvent;
import com.example.guiex1.utils.observer.Observer;
import com.example.guiex1.utils.paging.Page;
import com.example.guiex1.utils.paging.Pageable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendController implements Observer<Event> {
    UtilizatorService service;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    @FXML
    private Label Label;
    @FXML
    private TableView<Utilizator> tableView;
    @FXML
    private TableColumn<Utilizator, String> FirstName;
    @FXML
    private TableColumn<Utilizator, String> LastName;
    @FXML
            private Button buttonNext;
    @FXML
            private Button buttonPrevious;

    @FXML
            private Label label;
    Stage dialogStage;
    Utilizator utilizator;
    private int pageSize = 1;
    private int currentPage = 0;
    private int totalNumberOfElements = 0;

    public void setService(UtilizatorService service,  Stage stage, Utilizator u) {
        this.service = service;
        this.dialogStage=stage;
        this.utilizator =u;
        Label.setText(utilizator.getFirstName() + " " + utilizator.getLastName());
        service.addObserver(this);
        initModel();
    }


    @Override
    public void update(Event event) {
        this.utilizator = service.searchUser(this.utilizator.getId());
        if(event.getClass()==FriendshipEntityChangeEvent.class) {
            if(((FriendshipEntityChangeEvent) event).getData().getId().getRight().equals(utilizator.getId())) {
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "New ","Ai o cerere de prietenie noua");
            }
        }
        initModel();
    }

    private void initModel() {
        Page<Utilizator> page = service.findAllOnPageS(utilizator.getId(), new Pageable(pageSize,currentPage));
        int maxPage = (int) Math.ceil((double) page.getTotalNumberOfElements() / pageSize) - 1;
        if (maxPage == -1) {
            maxPage = 0;
        }
        if (currentPage > maxPage) {
            currentPage = maxPage;
            page = service.findAllOnPageS(utilizator.getId(), new Pageable(pageSize,currentPage));
        }
        totalNumberOfElements = page.getTotalNumberOfElements();
        buttonPrevious.setDisable(currentPage == 0);
        buttonNext.setDisable((currentPage + 1) * pageSize >= totalNumberOfElements);
        List<Utilizator> users = StreamSupport.stream(page.getElementsOnPage().spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(users);
        label.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
    }

    public void handleRemove(ActionEvent event) {
        Utilizator user=(Utilizator) tableView.getSelectionModel().getSelectedItem();
        if (user!=null) {
            Tuple<Long, Long> id = new Tuple<>(user.getId(), utilizator.getId());
            Prietenie deleted= service.deleteFriendship(id);
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"Delete friendship","Prietenia a fost stearsa");
        }
        else MessageAlert.showErrorMessage(null, "NU ati selectat nici un utilizator");
    }

    @FXML
    public void initialize() {
        LastName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        FirstName.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableView.setItems(model);
    }

    public void handleAdd(ActionEvent event) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/add-friend-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Adaugare prieteni");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AddFriendController addfriendController = loader.getController();
            addfriendController.setService(service, dialogStage,this.utilizator);
            dialogStage.show();

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    public void handleRequest(ActionEvent event) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/friend-requests-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friend Requests");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            FriendRequestController friendRequestController = loader.getController();
            friendRequestController.setService(service, dialogStage, this.utilizator);

            dialogStage.show();

        } catch ( IOException e) {
            e.printStackTrace();
        }

    }

    public void handleChange(ActionEvent actionEvent) {
        try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../views/change-view.fxml"));

                AnchorPane root = (AnchorPane) loader.load();

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Change");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                //dialogStage.initOwner(primaryStage);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                ChangeController registerController = loader.getController();
                registerController.setService(service, dialogStage, utilizator);

                dialogStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    public void handleOpenChat(ActionEvent actionEvent){
        Utilizator friend=(Utilizator) tableView.getSelectionModel().getSelectedItem();
        if(friend==null){
            MessageAlert.showErrorMessage(null,"Vorbesti singur");
            return;
        }
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/message-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("ChatGPT");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            MessageController messageController = loader.getController();
            messageController.setService(service, this.utilizator,friend);

            dialogStage.show();

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    public void handleGroupChat(ActionEvent actionEvent) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/group-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Group Chat");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            GroupController groupController = loader.getController();
            groupController.setService(service, dialogStage, utilizator);

            dialogStage.show();

        } catch ( IOException e) {
            e.printStackTrace();
        }

    }
    public void handleNext(ActionEvent actionEvent) {
        currentPage ++;
        initModel();
    }

    public void handlePrevious (ActionEvent actionEvent) {
        currentPage --;
        initModel();
    }

    public void handleOpenProfilePage(ActionEvent actionEvent) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/show-profile-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Profile page");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            Profile profile = service.getProfile(utilizator.getId());

            ProfileController profileController = loader.getController();
            profileController.setService(service, profile, utilizator);

            dialogStage.show();

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }
}
