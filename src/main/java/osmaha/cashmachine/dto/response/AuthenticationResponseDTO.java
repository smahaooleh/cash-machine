package osmaha.cashmachine.dto.response;

public class AuthenticationResponseDTO {
    private String cardNumber;
    private String token;

    public AuthenticationResponseDTO(String cardNumber, String token) {
        this.cardNumber = cardNumber;
        this.token = token;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
