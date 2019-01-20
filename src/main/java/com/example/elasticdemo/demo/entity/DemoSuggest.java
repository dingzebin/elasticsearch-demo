package com.example.elasticdemo.demo.entity;

import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @description:
 * @auther: dzeb
 * @create: 2019-01-20
 */
@Document(indexName = "demo", type = "demo-suggest")
public class DemoSuggest {
		@CompletionField()
		private String title;
}
