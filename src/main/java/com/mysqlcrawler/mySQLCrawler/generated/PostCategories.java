package com.mysqlcrawler.mySQLCrawler.generated;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "post_categories")
public class PostCategories {

 @EmbeddedId
 private PostCategoriesId id;

 @ManyToOne
 @JoinColumn(name = "category_id")
 private Categories categories;

 @ManyToOne
 @JoinColumn(name = "post_id")
 private Posts posts;

}
