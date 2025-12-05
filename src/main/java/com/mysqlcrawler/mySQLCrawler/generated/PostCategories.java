package com.mysqlcrawler.mySQLCrawler.generated;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

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
