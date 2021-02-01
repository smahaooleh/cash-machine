package osmaha.cashmachine.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "amount")
    private double amount;

    @Column(name = "balance_after")
    private double balanceAfter;

    @Column(name = "created")
    private LocalDateTime created;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TransactionType type;

    public Transaction() {
    }

    public Transaction(User user, double amount, double balanceAfter, LocalDateTime created, TransactionType type) {
        this.user = user;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.created = created;
        this.type = type;
    }

    public Transaction(Long id, User user, double amount, double balanceAfter, LocalDateTime created, TransactionType type) {
        this.id = id;
        this.user = user;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.created = created;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public String getUserCardNumber() {
        return user.getCardNumber();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalanceAfter() { return balanceAfter; }

    public void setBalanceAfter(double balanceAfter) { this.balanceAfter = balanceAfter; }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
