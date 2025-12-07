package com.mysqlcrawler.mySQLCrawler.dynamic;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "comments")
public class Comments {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "content")
	private String content;

	@Column(name = "created_at")
	private java.sql.Timestamp createdAt;

	@ManyToOne
	@JoinColumn(name = "post_id")
	private Posts posts;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Users users;

}
