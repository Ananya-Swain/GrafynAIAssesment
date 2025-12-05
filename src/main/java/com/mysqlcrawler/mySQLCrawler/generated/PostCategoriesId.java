package com.mysqlcrawler.mySQLCrawler.generated;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class PostCategoriesId implements Serializable {

 private Integer postId;

 private Integer categoryId;

}