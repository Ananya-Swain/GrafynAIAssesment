package com.mysqlcrawler.mySQLCrawler.generated;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "posts")
public class Posts {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "title")
	private String title;

	@Column(name = "content")
	private String content;

	@Column(name = "created_at")
	private java.sql.Timestamp createdAt;

	@Column(name = "updated_at")
	private java.sql.Timestamp updatedAt;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Users users;


	@ManyToMany(mappedBy = "posts")
	private List<Categories> categories;
	@OneToMany(mappedBy = "posts")
	private List<Comments> comments;
	@OneToMany(mappedBy = "posts")
	private List<Likes> likes;
}
