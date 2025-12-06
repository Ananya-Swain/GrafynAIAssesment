package com.mysqlcrawler.mySQLCrawler.generated;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "likes")
public class Likes {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "created_at")
	private java.sql.Timestamp createdAt;

	@ManyToOne
	@JoinColumn(name = "post_id")
	private Posts posts;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Users users;

}
