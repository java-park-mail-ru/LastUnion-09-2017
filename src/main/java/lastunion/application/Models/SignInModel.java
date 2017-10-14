package lastunion.application.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SignInModel {

    private final String userName;
    private final String userPassword;

    @JsonCreator
    public SignInModel(@JsonProperty("userName") String name, @JsonProperty("userPassword") String password){
        this.userName = name;
        this.userPassword = password;
    }

    public String getUserName(){
        return userName;
    }
    public String getUserPassword(){ return userPassword;  }
}
