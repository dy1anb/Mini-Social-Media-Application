package controllers;
import entities.LoginLog;
import entities.UserAccountEntity;
import gateways.*;
import org.json.simple.*;

import presenters.LoginPresenter;
import usecases.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A Controller class which deals with users logging in and creating new accounts.
 *
 * @author Haider Bokhari
 * @version 2.0
 *
 */

public class LoginController {
    private ArrayList<String[]> Accounts;
    protected UserAccountManager uam;
    protected LoginLogManager llm;
    private final LoginPresenter lp;

    private final LoginLogGateway llg;
    private final UserAccountGateway uag;

    /**
     * Constructor for LoginController object.
     */
    public LoginController(){
        //The gateways that will be used to serialize data
        this.llg = new LoginLogGateway();
        this.uag = new UserAccountGateway();

        //Use gateways to initialize all use cases and managers
        this.llm = llg.deserializeData();
        this.uam = uag.deserializeData();

        //Get list of all existing accounts from the user manager
        this.Accounts = uam.getAccountInfo();
        this.lp = new LoginPresenter();
    }

    /**
     * Called to create a new account for a user.
     */
    public JSONObject CreateAccount(JSONObject obj, boolean isMade){

        // String Username, String Password, String type, String q1, String ans1,
        //                                    String q2, String ans2, String q3, String ans3, boolean security

        String Username = obj.get("username").toString();
        String Password = obj.get("password").toString();
        String type = obj.get("userType").toString();
        String q1 = obj.get("securityQuestion1").toString();
        String ans1 = obj.get("securityAnswer1").toString();
        String q2 = obj.get("securityQuestion2").toString();
        String ans2 = obj.get("securityAnswer2").toString();
        boolean security = (Boolean) obj.get("setup");


        int UsernameCheck = 0;
        //Loops through all existing usernames.
        for (String[] users : Accounts){
            if (users[0].equals(Username)){
                //If the Username the user entered already exists then UsernameCheck counter is increased.
                UsernameCheck++;
            }
        }
        //Check for whitespace
        if (Username.contains(" "))
            return lp.noWhiteSpace();
        //Check Username Minimum Length
        if (Username.length() < 1)
            return lp.EmptyName();

        //If the counter is 0, that means the username isn't taken and can be set.
        if (UsernameCheck != 0)
            return lp.UsernameTaken();

        //Check minimum password length for security
        if(Password.length() < 6)
            return lp.EmptyPassword();

        //Security Questions if forget password or want to reset
        lp.SecurityQuestion1();
        lp.SecurityQuestion2();

        //Add account to the user manager and update the Accounts Arraylist
        uam.addAccount(Username, Password, type, security,
                q1, q2, ans1, ans2);
        uag.serializeData(uam);
        this.Accounts = uam.getAccountInfo();

        return lp.AccountMade();
    }

    /**
     * Called to let user login to an existing account in the database.
     */
    public JSONObject login(String Username, String Password){
        boolean UsernameExists = false;
        boolean PasswordExists = false;

        //Go through all existing account to see if username entered exists in the database.
        for (String[] users : Accounts){
            if (users[0].equals(Username)){
                UsernameExists = true;
                //If it does exist, check if the password matches.
                if (users[1].equals(Password)){
                    PasswordExists = true;
                }
            }
        }

        //If the username doesn't exist or password doesn't match, log a failed login.
        if (!(UsernameExists && PasswordExists)){
            UpdateLogs(Username, "Failed Login");
            //If past 3 logs are failed logins, lock the account.
            if (suspiciousLogs(Username)){
                lockOut(Username);
                return lp.AccountLocked();
            }
            else
                return lp.IncorrectCredentials();
        }

        UserAccountEntity Account = uam.getUserAccount(Username);
        //If account is locked, don't let the user login.
        if(Account.isLocked())
            return lp.AccountLocked();

        return lp.SuccessfulLogin(Account.getJSON());
    }

    /**
     * Locks a user, which prevents them from logging in until an Admin unlocks their account.
     */
    public void lockOut(String Username){
        UserAccountEntity Account = uam.getUserAccount(Username);
        Account.Lock();
        uam.updateAccount(Username, Account);
        uag.serializeData(uam);
    }

    /**
     * Returns true if past 3 logins were failed logins, false otherwise.
     */
    public boolean suspiciousLogs(String Username){
        //Check if user has logs
        if (!llm.checkLogExists(Username))
            return false;

        ArrayList<LoginLog> RecentLogs = llm.getUserLogs(Username);
        int strike = 0;

        //Check past 3 logs, if they are all failed logins then account is suspicious.
        for (LoginLog log : RecentLogs) {
            if (log.getCondition().equals("Failed Login"))
                strike++;
        }
        return strike == 3;
    }

    /**
     * Lets user change password if they can answer 3 security questions correctly which were set when making an account.
     */
    public boolean verifySecurityAnswers(String User, String a1, String a2){
        UserAccountEntity Account = uam.getUserAccount(User);

        lp.SecurityQuestion1();
        lp.SecurityQuestion2();

        //Check if answers to security questions match.
        return a1.equals(Account.getSecurityAns(1)) && a2.equals(Account.getSecurityAns(2));
    }

    public void resetPassword(String user, String newPassword){
        UserAccountEntity Account = uam.getUserAccount(user);

        //Update password
        Account.setPassword(newPassword);
        uam.updateAccount(Account.getUsername(), Account);
        uag.serializeData(uam);
    }

    /**
     * Update the logs database.
     */
    public void UpdateLogs(String Username, String type){
        //Get current time and convert it to string/
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat dateForm = new SimpleDateFormat(pattern);
        String dateString = dateForm.format(Calendar.getInstance().getTime());

        //Add log.
        llm.addToLoginLogSet(type, Username, dateString);
        llg.serializeData(llm);

    }

    public JSONArray accountJson(String username){
        return uam.getUserAccount(username).getJSON();
    }

}