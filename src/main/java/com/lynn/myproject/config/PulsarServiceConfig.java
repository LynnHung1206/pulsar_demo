package com.lynn.myproject.config;

import com.lynn.myproject.listener.PulsarListener;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.common.policies.data.TenantInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @Author: Lynn on 2024/8/16
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class PulsarServiceConfig {

  private final PulsarListener pulsarListener;

  @Value("${PULSAR_ADMIN_URL}")
  private String pulsarAdminUrl;

  @Value("${PULSAR_SERVICE_URL}")
  private String pulsarServiceUrl;


  @PostConstruct
  public void initAll() {
    try {
      this.init();
      pulsarListener.startConsume(pulsarAdminUrl);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public void init() {
    log.info("====================>  開始進行建立分區topic PulsarServiceConfig");
    try {
      String topicName = "persistent://study/app1/partitionTopic";
      int numPartitions = 4;
      createPartitionedTopic(topicName, numPartitions);
    } catch (PulsarClientException e) {
      log.error("建立分區topic error", e);
    }
    log.info("====================>  開始進行建立分區topic 成功");
  }

  public void createPartitionedTopic(String topicName, int numPartitions) throws PulsarClientException {
    // admin端的是8080
    try (PulsarAdmin pulsarAdmin = PulsarAdmin.builder()
        .serviceHttpUrl(pulsarServiceUrl)
        .build()) {

      if (!pulsarAdmin.tenants().getTenants().contains("study")) {
        TenantInfo root = TenantInfo.builder()
            .adminRoles(Collections.singleton("pulsar-cluster"))
            .allowedClusters(Collections.singleton("standalone"))
            .build();
        pulsarAdmin.tenants().createTenant("study", root);
      }

      if (!pulsarAdmin.namespaces().getNamespaces("study").contains("study/app1")) {
        pulsarAdmin.namespaces().createNamespace("study/app1");
      }

      try {
        List<String> topicList = pulsarAdmin.topics().getList("study/app1");
        log.info("====================> topicList={}", topicList);

        if (topicList.contains(topicName)) {
          log.info("Topic with the same name already exists as a non-partitioned topic: {}", topicName);
          return;
        }
        List<String> partitionedTopicList = pulsarAdmin.topics().getPartitionedTopicList("study/app1");

        log.info("====================> partitionedTopicList={}", partitionedTopicList);
        log.info("topicName={}", topicName);
        if (!partitionedTopicList
            .contains(topicName)) {
          pulsarAdmin.topics().createPartitionedTopic(topicName, numPartitions);
          System.out.println("====================> Partitioned topic " + topicName + " created with " + numPartitions + " partitions.");
        } else {
          System.out.println("====================> Topic " + topicName + " already exists.");
        }
      } catch (PulsarAdminException e) {
        log.error("create topic failed", e);
      }
    } catch (PulsarAdminException e) {
      throw new RuntimeException(e);
    }
  }


}
