package com.spring.kafka.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.IOException;

@Configuration
public class ElasticSearchConfig {

    @Bean
    @Scope("prototype")
    public RestClient createClient() throws IOException {
    //    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider(); Estas credenciales son para AWS
    //    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("", ""));

    //    esta configuracion tambien es para AWS
    //    RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("", 443, "http")).setHttpClientConfigCallback((config) -> config.setDefaultCredentialsProvider(credentialsProvider)));

    //  Forma nueva de hacerlo
        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));
        RestClient restClient = builder.build();
        restClient.close();

        return restClient;
    }


}
