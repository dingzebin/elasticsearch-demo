package com.example.elasticdemo.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.completion.Completion;

import java.io.Serializable;

/**
 * @description:
 * @auther: dzeb
 * @create: 2019/1/19
 */
@Document(indexName = "demo", type = "demo")
public class Demo implements Serializable {
    @Id
    private String id;
    @Field(type = FieldType.Text, searchAnalyzer = "ik_max_word", analyzer = "ik_max_word", store = true)
    private String title;
    @Field(type = FieldType.Long, store = true)
    private Long createDate;
    @Field(type = FieldType.Text, searchAnalyzer = "ik_max_word", analyzer = "ik_max_word", store = true)
    private String creator;
    @Field(type = FieldType.Text, searchAnalyzer = "ik_max_word", analyzer = "ik_max_word", store = true)
    private String mainBody;
    @CompletionField(searchAnalyzer = "ik_max_word", analyzer = "ik_max_word")
    private Completion suggest;
    private String createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getMainBody() {
        return mainBody;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setMainBody(String mainBody) {
        this.mainBody = mainBody;
    }

    public Completion getSuggest() {
        return suggest;
    }

    public void setSuggest(Completion suggest) {
        this.suggest = suggest;
    }
}
