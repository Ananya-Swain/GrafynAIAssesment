package com.mysqlcrawler.mySQLCrawler.generated;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class Users {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(name = "id")
 private Integer id;

 @Column(name = "uname")
 private String uname;

 @Column(name = "email")
 private String email;

 @Column(name = "password")
 private String password;

 @Column(name = "created_at")
 private java.sql.Timestamp createdAt;

}
