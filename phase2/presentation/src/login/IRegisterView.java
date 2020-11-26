package login;

import common.IView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public interface IRegisterView extends IView {
    String getUsername();
    void setUsername(String username);
    String getFirstName();
    void setFirstName(String firstName);
    String getLastName();
    void setLastName(String lastName);
    String getPassword();
    void setPassword(String password);
    String getConfirmPassword();
    void setConfirmPassword(String password);
    void setErrorMsg(String error);
    TextField getUsernameField();
    TextField getFirstNameField();
    TextField getLastNameField();
    PasswordField getPasswordField();
    PasswordField getConfirmPasswordField();

    EventHandler<ActionEvent> getBackButtonAction();
    void setBackButtonAction(EventHandler<ActionEvent> eventHandler);
    EventHandler<ActionEvent> getRegisterButtonAction();
    void setRegisterButtonAction(EventHandler<ActionEvent> eventHandler);
}
