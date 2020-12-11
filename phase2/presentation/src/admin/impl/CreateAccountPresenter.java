package admin.impl;

import common.UserAccountHolder;
import controllers.AdminController;
import javafx.event.ActionEvent;
import admin.ICreateAccountPresenter;
import admin.ICreateAccountView;
import model.UserAccount;
import org.json.simple.JSONObject;
import util.TextResultUtil;

public class CreateAccountPresenter implements ICreateAccountPresenter {
    private final ICreateAccountView view;
    private final AdminController ac;

    public CreateAccountPresenter(ICreateAccountView view) {
        this.view = view;
        getUserData();
        this.ac = new AdminController(this.view.getSessionUsername());
        init();
    }

    @Override
    public void createAccountButtonAction(ActionEvent actionEvent) {
        clearResultText();

        JSONObject queryJson = constructAccountJson();
        JSONObject responseJson = ac.createAccount(queryJson);
        setResultText(String.valueOf(responseJson.get("result")), String.valueOf(responseJson.get("status")));
        if (String.valueOf(responseJson.get("status")).equals("success")) init();
    }

    @Override
    public void setResultText(String resultText, String status) {
        this.view.setResultText(resultText);
        TextResultUtil.getInstance().addPseudoClass(status, this.view.getResultTextControl());
        if (status.equals("error") || status.equals("warning")) {
            TextResultUtil.getInstance().addPseudoClass(status, this.view.getUsernameField());
            TextResultUtil.getInstance().addPseudoClass(status, this.view.getPasswordField());
            TextResultUtil.getInstance().addPseudoClass(status, this.view.getConfirmPasswordField());
        }
    }

    @Override
    public void init() {
        this.view.setCreateAccountButtonAction(this::createAccountButtonAction);
    }

    @Override
    public void getUserData() {
        UserAccountHolder holder = UserAccountHolder.getInstance();
        UserAccount account = holder.getUserAccount();
        this.view.setSessionUsername(account.getUsername());
        this.view.setSessionUserType(account.getUserType());
    }

    @SuppressWarnings("unchecked")
    private JSONObject constructAccountJson() {
        JSONObject queryJson = new JSONObject();
        queryJson.put("username", this.view.getUsername());
        queryJson.put("userType", this.view.getUserType().toLowerCase());
        queryJson.put("password", this.view.getPassword());
        queryJson.put("confirmPassword", this.view.getConfirmPassword());
        queryJson.put("setup", Boolean.TRUE);
        return queryJson;
    }

    private void clearResultText() {
        this.view.setResultText("");
        TextResultUtil.getInstance().removeAllPseudoClasses(this.view.getResultTextControl());
        TextResultUtil.getInstance().removeAllPseudoClasses(this.view.getUsernameField());
        TextResultUtil.getInstance().removeAllPseudoClasses(this.view.getPasswordField());
        TextResultUtil.getInstance().removeAllPseudoClasses(this.view.getConfirmPasswordField());
    }
}
