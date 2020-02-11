package com.allstars.recipie_management_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@JsonIgnoreProperties(value={"password"}, allowSetters= true)
public class User {
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "userid", columnDefinition = "CHAR(32)")
    @Id
    private String uuid;
    @Column
    private String first_name;
    @Column
    private String last_name;
    @Column(unique = true, length = 32)
    private String emailId;
    @Column(nullable=false)
    private String password;
    @Column
    private Date account_created;
    @Column
    private Date account_updated;


    public User(String uuid, String first_name, String last_name, String emailId, String password, Date account_created, Date account_updated) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.emailId = emailId;
        this.password = password;
    }

    public User()
    {

    }



    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getAccount_created() {
        return account_created;
    }

    public void setAccount_created(Date account_created) {
        if (this.account_created == null) {
            this.account_created = new Date();
        }
    }

    public Date getAccount_updated() {
        return account_updated;
    }

    public void setAccount_updated(Date account_updated) {
        this.account_updated = new Date();
    }
}
