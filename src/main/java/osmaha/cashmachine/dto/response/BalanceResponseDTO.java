package osmaha.cashmachine.dto.response;

public class BalanceResponseDTO {

    private double balance;

    public BalanceResponseDTO(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
