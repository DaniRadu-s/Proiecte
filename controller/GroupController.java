package com.example.guiex1.controller;

import com.example.guiex1.domain.Message;
import com.example.guiex1.domain.Tuple;
import com.example.guiex1.domain.Utilizator;
import com.example.guiex1.services.UtilizatorService;
import com.example.guiex1.utils.events.Event;
import com.example.guiex1.utils.events.MessageEntityChangeEvent;
import com.example.guiex1.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GroupController implements Observer<Event> {
    UtilizatorService service;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();
    ObservableList<Utilizator> model1 = FXCollections.observableArrayList();
    Stage dialogStage;
    Utilizator utilizator;
    @FXML
    TableView<Utilizator> tableView1;
    @FXML
    TableView<Utilizator> tableView2;
    @FXML
    TableColumn<Utilizator,String> columnTable11;
    @FXML
    TableColumn<Utilizator,String> columnTable12;
    @FXML
    TableColumn<Utilizator,String> columnTable21;
    @FXML
    TableColumn<Utilizator,String> columnTable22;
    @FXML
    TextField messageField;

    @Override
    public void update(Event event) {
        this.utilizator = service.searchUser(this.utilizator.getId());
        for(Utilizator u : model1.stream().collect(Collectors.toList()))
            if(event.getClass()== MessageEntityChangeEvent.class){
                Message msg = (Message) ((MessageEntityChangeEvent) event).getData();
                initModel();
            }
    }

    private void initModel() {
        tableView1.setItems(model);
        tableView2.setItems(model1);
    }

    public void initialize() {
        columnTable11.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        columnTable12.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        columnTable21.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        columnTable22.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));

        tableView1.setOnMouseClicked(event -> {
            Utilizator selectedItem = tableView1.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                model1.add(selectedItem);
                model.remove(selectedItem);
            }
        });

        tableView2.setOnMouseClicked(event -> {
            Utilizator selectedItem = tableView2.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                model.add(selectedItem);
                model1.remove(selectedItem);
            }
        });

        tableView1.setItems(model);
        tableView2.setItems(model1);
    }
    public void setService(UtilizatorService service, Stage stage, Utilizator u) {
        this.service = service;
        this.dialogStage = stage;
        this.utilizator = u;
        service.addObserver(this);
        Iterable<Tuple<Utilizator,String>> messages = utilizator.getFriends();
        List<Utilizator> users = StreamSupport.stream(messages.spliterator(), false)
                .filter(ut->"active".equals(ut.getRight())).map(ut->ut.getLeft())
                .collect(Collectors.toList());
        model.addAll(users);
        initModel();
    }

    public void handleSend(ActionEvent actionEvent) {
        String text = messageField.getText();
        for(Utilizator u : model1.stream().collect(Collectors.toList())) {
            Message msg = new Message(utilizator, List.of(u), text, LocalDateTime.now(), null);
            service.addMessage(msg);
        }
        messageField.clear();
    }
}
