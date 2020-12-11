package admin.impl;

import adapter.LoginLogAdapter;
import adapter.UserAccountAdapter;
import admin.IUnlockAccountsPresenter;
import admin.IUnlockAccountsView;
import common.UserAccountHolder;
import controllers.AdminController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import model.LoginLog;
import model.UserAccount;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.DateTimeUtil;
import util.TextResultUtil;
import java.util.List;

public class UnlockAccountsPresenter implements IUnlockAccountsPresenter {
    private final IUnlockAccountsView view;
    private final AdminController ac;
    private UserAccount selectedAccount;

    public UnlockAccountsPresenter(IUnlockAccountsView view) {
        this.view = view;
        getUserData();
        this.ac = new AdminController(this.view.getSessionUsername());
        init();
    }

    @Override
    public void unlockButtonAction(ActionEvent actionEvent) {
        clearResultText();

        JSONObject responseJson = ac.unlockAccount(selectedAccount.getUsername());
        setResultText(String.valueOf(responseJson.get("result")), String.valueOf(responseJson.get("status")));
        if (responseJson.get("status").equals("success")) init();
    }

    @Override
    public void setResultText(String resultText, String status) {
        this.view.setResultText(resultText);
        TextResultUtil.getInstance().addPseudoClass(status, this.view.getResultTextControl());
    }

    @Override
    public List<UserAccount> getUserAccounts() {
        JSONObject responseJson = ac.getAllAccounts();
        return UserAccountAdapter.getInstance().adaptData((JSONArray) responseJson.get("data"));
    }

    @Override
    public void displayUserAccounts(List<UserAccount> accounts) {
        this.view.getUsernameColumn().setCellValueFactory(new PropertyValueFactory<>("username"));
        this.view.getUserTypeColumn().setCellValueFactory(new PropertyValueFactory<>("userType"));
        this.view.getLockedColumn().setCellValueFactory(param -> param.getValue().lockedProperty());
        this.view.getUserTable().setItems(FXCollections.observableList(accounts));
        this.view.getUserTable().getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleSelect(newValue));
    }

    @Override
    public void displayUserAccountDetails(UserAccount account) {
        this.selectedAccount = account;
        this.view.setUsername(account.getUsername());
        this.view.setUserType(account.getUserType());
        List<LoginLog> logs = getUserLoginLogs(account.getUsername());
        displayUserLoginLogs(logs);
    }

    @Override
    public List<LoginLog> getUserLoginLogs(String username) {
        JSONObject responseJson = ac.getLoginLogs(username);
        return LoginLogAdapter.getInstance().adaptData((JSONArray) responseJson.get("data"));
    }

    @Override
    public void displayUserLoginLogs(List<LoginLog> logs) {
        DateTimeUtil.getInstance().setLoginDateTimeCellFactory(this.view.getLoginTimeColumn());
        this.view.getLoginTimeColumn().setCellValueFactory(new PropertyValueFactory<>("loginTime"));
        this.view.getSuccessColumn().setCellValueFactory(param -> param.getValue().successProperty());
        this.view.getLogsTable().setItems(FXCollections.observableList(logs));
    }

    @Override
    public void init() {
        this.view.setUnlockButtonAction(this::unlockButtonAction);
        List<UserAccount> accounts = getUserAccounts();
        displayUserAccounts(accounts);
    }

    @Override
    public void getUserData() {
        UserAccountHolder holder = UserAccountHolder.getInstance();
        UserAccount account = holder.getUserAccount();
        this.view.setSessionUsername(account.getUsername());
        this.view.setSessionUserType(account.getUserType());
    }

    private void clearResultText() {
        this.view.setResultText("");
        TextResultUtil.getInstance().removeAllPseudoClasses(this.view.getResultTextControl());
    }

    private void handleSelect(UserAccount account) {
        getUserLoginLogs(account.getUsername());
        displayUserAccountDetails(account);
    }
}
