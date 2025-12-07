package com.mysqlcrawler.mySQLCrawler.dynamic;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "post_categories")
public class PostCategories {

	@EmbeddedId
	private PostCategoriesId id;

	@MapsId("categoryId")	@ManyToOne
	@JoinColumn(name = "category_id")
	private Categories categories;

	@MapsId("postId")	@ManyToOne
	@JoinColumn(name = "post_id")
	private Posts posts;

}
