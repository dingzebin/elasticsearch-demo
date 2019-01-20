package com.example.elasticdemo.demo.repository;

import com.example.elasticdemo.demo.entity.Demo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DemoRepository extends ElasticsearchRepository<Demo, String> {
}
