package com.kris.docker_spring_boot.server.elasticsearch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 查询
 *
 * @author Beck
 * @date 2018年2月6日
 */
public class TestESQuery {

    private static final String INDEX = "zcestestrecord";

    private static final String TYPE = "product";

    private RestHighLevelClient client;

    // 词条查询
    @Test
    public void isExit() {

        GetIndexRequest request = new GetIndexRequest(INDEX);
        try {
            System.out.println(client.indices().exists(request, RequestOptions.DEFAULT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteIndex() {
        DeleteIndexRequest request = new DeleteIndexRequest("test");
        try {
            client.indices().delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("test");
        client.indices().create(request, RequestOptions.DEFAULT);
    }

    @Test
    public void insertDate() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        List<String> datas = Files.readAllLines(Paths.get("D:\\test-data.txt"));
        for (String line : datas) {
            IndexRequest request = new IndexRequest("test");
            bulkRequest.add(request.source(line, XContentType.JSON));
        }
        client.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    @Test
    public void search() {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(10);
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("srcIp", "1.1.4.4");
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("timestamp");
        rangeQueryBuilder.gte(1567545612);
        rangeQueryBuilder.lte(1857545612);
        BoolQueryBuilder boolq = QueryBuilders.boolQuery();
        boolq.must(matchQueryBuilder);
        boolq.must(rangeQueryBuilder);
        sourceBuilder.query(boolq);
        SearchRequest searchRequest = new SearchRequest("test");
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println("结果总数：" + response.getHits().getTotalHits().value);
            SearchHit[] sh = response.getHits().getHits();
            for (SearchHit documentFields : sh) {
                Map<String, Object> hit = documentFields.getSourceAsMap();
                for (String key : hit.keySet()) {
                    System.out.print(key + "=" + hit.get(key) + ",");
                }
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取客户端
    @Before
    public void getClient() {
        client = new RestHighLevelClient(RestClient.builder(
                // 集群节点
                new HttpHost("node2", 9200, "http")));
    }

    // 关闭客户端
    @After
    public void closeClient() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
