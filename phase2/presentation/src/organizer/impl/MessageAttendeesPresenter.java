package organizer.impl;

import adapter.UserAdapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import model.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import organizer.IMessageUsersPresenter;
import organizer.IMessageUsersView;
import util.TextResultUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageAttendeesPresenter implements IMessageUsersPresenter {
    private IMessageUsersView view;
    private ObservableList<User> users;

    public MessageAttendeesPresenter(IMessageUsersView view) {
        this.view = view;
        init();
    }

    @Override
    public void sendButtonAction(ActionEvent actionEvent) {
        clearResult();

        JSONObject queryJson = constructMessageJson();
        //JSONObject responseJson = oc.sendAllSpeakers(queryJson);
        JSONObject responseJson = new JSONObject();
        setResultText(String.valueOf(responseJson.get("result")), String.valueOf(responseJson.get("status")));
    }

    @Override
    public void selectAllAction(ActionEvent actionEvent) {
        boolean checked = this.view.getSelectAll().isSelected();
        for (User u : this.users)
            u.setChecked(checked);
    }

    @Override
    public void setResultText(String resultText, String status) {
        this.view.setResultText(resultText);
        TextResultUtil.getInstance().addPseudoClass(status, this.view.getResultTextControl());
        if (status.equals("warning") || status.equals("error")) {
            TextResultUtil.getInstance().addPseudoClass(status, this.view.getRecipientsField());
            TextResultUtil.getInstance().addPseudoClass(status, this.view.getContentArea());
        }
    }

    @Override
    public List<User> getAllUsers() {
        //JSONObject responseJson = oc.createRoom(queryJson);
        JSONObject responseJson = new JSONObject();
        setResultText(String.valueOf(responseJson.get("result")), String.valueOf(responseJson.get("status")));
        return UserAdapter.getInstance().adaptData((JSONArray) responseJson.get("data"));
    }

    @Override
    public void displayUserList(List<User> users) {
        this.users = FXCollections.observableArrayList(users);
        //This callback tell the cell how to bind the user model 'selected' property to the cell, itself
        this.view.getCheckedColumn().setCellValueFactory(param -> param.getValue().getSelected());
        this.view.getFirstNameColumn().setCellValueFactory(new PropertyValueFactory<>("firstName"));
        this.view.getLastNameColumn().setCellValueFactory(new PropertyValueFactory<>("lastName"));
        this.view.getUsernameColumn().setCellValueFactory(new PropertyValueFactory<>("username"));
        this.view.getUserTable().setItems(this.users);
        this.view.getUserTable().getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateRecipientList());
    }


    @Override
    public void updateRecipientList() {
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (User u : this.users) {
            if (u.getChecked()) {
                sb.append(prefix);
                prefix = ", ";
                sb.append(u.getUsername());
            }
        }
        this.view.setRecipients(sb.toString());
    }

    @Override
    public void init() {
        List<User> users = getAllUsers();
        displayUserList(users);
        this.view.setSendButtonAction(this::sendButtonAction);
        this.view.setSelectAllAction(this::selectAllAction);
    }

    @SuppressWarnings("unchecked")
    private JSONObject constructMessageJson() {
        JSONObject queryJson = new JSONObject();
        JSONArray recipients = new JSONArray();
        recipients.addAll(Arrays.asList(this.view.getRecipients().split(", ")));
        queryJson.put("sender", this.view.getSender());
        queryJson.put("recipients", recipients);
        queryJson.put("content", this.view.getContent());
        return queryJson;
    }

    private void clearResult() {
        this.view.setResultText("");
        TextResultUtil.getInstance().removeAllPseudoClasses(this.view.getResultTextControl());
        TextResultUtil.getInstance().removeAllPseudoClasses(this.view.getContentArea());
        TextResultUtil.getInstance().removeAllPseudoClasses(this.view.getRecipientsField());
    }
}
