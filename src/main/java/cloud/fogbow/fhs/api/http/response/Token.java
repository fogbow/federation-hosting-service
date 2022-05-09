package cloud.fogbow.fhs.api.http.response;

public class Token {
    private String token;
    
    public Token(String encryptedToken) {
        this.token = encryptedToken;
    }

    public String getToken() {
        return token;
    }
}
