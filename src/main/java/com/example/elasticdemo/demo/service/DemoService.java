package com.example.elasticdemo.demo.service;

import com.example.elasticdemo.demo.entity.Demo;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;

public interface DemoService {

		void insert(Demo demo);

		AggregatedPage<Demo> search(String searchKey, Integer num, Integer size);

		void delete(String id);

		void createIndex();

		Object suggest(String key);
}
