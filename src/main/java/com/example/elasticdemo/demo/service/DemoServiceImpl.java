package com.example.elasticdemo.demo.service;

import com.alibaba.fastjson.JSON;
import com.example.elasticdemo.demo.entity.Demo;
import com.example.elasticdemo.demo.repository.DemoRepository;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.completion.Completion;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @description:
 * @auther: dzeb
 * @create: 2019/1/19
 */
@Service
public class DemoServiceImpl implements DemoService{
		@Autowired
		DemoRepository demoRepository;
		@Autowired
		ElasticsearchTemplate elasticsearchTemplate;
		@Override
		public void insert(Demo demo) {
				this.createSuggest(demo);
				demoRepository.save(demo);
		}

		private void createSuggest(Demo demo) {
				AnalyzeRequestBuilder requestBuilder = new AnalyzeRequestBuilder(elasticsearchTemplate.getClient()
								, AnalyzeAction.INSTANCE, "demo", demo.getTitle(), demo.getMainBody());
				requestBuilder.setAnalyzer("ik_smart");
				AnalyzeResponse response = requestBuilder.get();
				List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
				List<String> suggets = new ArrayList<String>();
				for (AnalyzeResponse.AnalyzeToken token : tokens) {
						if ("<NUM>".equals(token.getType()) || token.getTerm().length() < 2) {
								continue;
						}
						suggets.add(token.getTerm());
				}
				String[] temp = new String[suggets.size()];
				Completion completion = new Completion(suggets.toArray(temp));
				demo.setSuggest(completion);
		}

		@Override
		public AggregatedPage<Demo> search(String searchKey, Integer num, Integer size) {
				String preTag = "<font color='red'>";
				String postTag = "</font>";
				SearchQuery query = new NativeSearchQueryBuilder().withFields("id", "title","creator","createDate","mainBody")
								.withQuery(buildQueryBuild(searchKey))
								.withHighlightFields(new HighlightBuilder.Field("title").preTags(preTag).postTags(postTag),
												new HighlightBuilder.Field("mainBody").preTags(preTag).postTags(postTag))
								.withPageable(PageRequest.of(num, size)).build();
				AggregatedPage<Demo> demos = elasticsearchTemplate.queryForPage(query, Demo.class, new SearchResultMapper() {
						@Override
						public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
								List<Demo> chunks = new LinkedList<>();
								for (SearchHit searchHit : response.getHits()) {
										if (response.getHits().getHits().length <= 0) {
												return null;
										}

										Demo demo = JSON.parseObject(JSON.toJSONString(searchHit.getSourceAsMap()), Demo.class);
										//name or memoe
										HighlightField title = searchHit.getHighlightFields().get("title");
										if (title != null) {
												demo.setTitle(title.fragments()[0].toString());
										}
										HighlightField mainBody = searchHit.getHighlightFields().get("mainBody");
										if (mainBody != null) {
												demo.setMainBody(mainBody.fragments()[0].toString());
										}

										chunks.add(demo);
								}
								if (!chunks.isEmpty()) {
										return new AggregatedPageImpl<>((List<T>)chunks);
								}

								return null;
						}
				});
				return demos;
		}

		@Override
		public void delete(String id) {
				demoRepository.deleteById(id);
		}

		@Override
		public void createIndex() {
				elasticsearchTemplate.putMapping(Demo.class);

		}

		@Override
		public Object suggest(String key) {
				CompletionSuggestionBuilder s = SuggestBuilders.completionSuggestion("suggest").prefix(key).size(100);
				SuggestBuilder b = new SuggestBuilder().addSuggestion("suggest", s);
				SearchResponse response = elasticsearchTemplate.suggest(b, "demo");
				Set<String> suggests = new HashSet<>();

				List entries = response.getSuggest().getSuggestion("suggest").getEntries();
				for (Object entry : entries) {
						if (entry instanceof CompletionSuggestion.Entry) {
								CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) entry;

								if (item.getOptions().isEmpty()) {
										continue;
								}
								for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
										String tip = option.getText().string();
										suggests.add(tip);
								}
						}
				}
				return suggests;
		}

		private QueryBuilder buildQueryBuild(String searchKey) {
				return QueryBuilders.boolQuery().should(QueryBuilders.queryStringQuery(searchKey));
		}
}
