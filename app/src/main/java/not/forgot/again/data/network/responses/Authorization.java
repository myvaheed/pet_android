package not.forgot.again.data.network.responses;

import not.forgot.again.model.entities.User;

public class Authorization extends User {
    private String api_token;

    public Authorization(int id, String name, String password, String email) {
        super(id, name, password, email);
    }

    public String getApi_token() {
        return api_token;
    }

    public void setApi_token(String api_token) {
        this.api_token = api_token;
    }
}
