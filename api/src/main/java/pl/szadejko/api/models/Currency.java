package pl.szadejko.api.models;

import pl.szadejko.api.security.User;
import pl.szadejko.api.service.DbEntity;

import javax.persistence.*;

@Entity
@Table(name = "currency")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user", referencedColumnName = "id")
    User user;

    @Column(name = "pln")
    double pln;

    @Column(name = "usd")
    double usd;

    @Column(name = "gbp")
    double gbp;

    @Column(name = "eur")
    double eur;

    public Currency() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getPln() {
        return pln;
    }

    public void setPln(double pln) {
        this.pln = pln;
    }

    public double getUsd() {
        return usd;
    }

    public void setUsd(double usd) {
        this.usd = usd;
    }

    public double getGbp() {
        return gbp;
    }

    public void setGbp(double gbp) {
        this.gbp = gbp;
    }

    public double getEur() {
        return eur;
    }

    public void setEur(double eur) {
        this.eur = eur;
    }
}
