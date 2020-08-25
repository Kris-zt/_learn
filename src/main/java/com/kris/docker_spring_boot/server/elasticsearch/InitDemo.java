package com.kris.docker_spring_boot.server.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @Description: 获取Java High Level REST Client客户端
 * @author lgs
 * @date 2018年6月23日
 */
public class InitDemo {

    public static RestHighLevelClient getClient() {

        return new RestHighLevelClient(RestClient.builder(
                // 集群节点
                new HttpHost("node2", 9200, "http")));
    }
}
