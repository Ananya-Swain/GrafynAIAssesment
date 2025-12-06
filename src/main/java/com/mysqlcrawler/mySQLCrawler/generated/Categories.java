package com.mysqlcrawler.mySQLCrawler.generated;

import java.util.Set;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "categories")
public class Categories {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "category_name")
	private String categoryName;

	@ManyToMany
	@JoinTable(
		name = "post_categories",
		joinColumns = @JoinColumn(name = "category_id"),
		inverseJoinColumns = @JoinColumn(name = "post_id")
	)
	private Set<Posts> posts;
}
