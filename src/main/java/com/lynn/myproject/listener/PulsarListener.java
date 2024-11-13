package com.lynn.myproject.listener;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: Lynn on 2024/8/16
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PulsarListener {

  private PulsarClient client;
  private Consumer<byte[]> consumer;


  public void startConsume(String serviceUrl) throws PulsarClientException {
    log.info("====================>  開始進行監聽 PulsarListener");
    // 客戶端的路徑是6650
    client = PulsarClient.builder().serviceUrl(serviceUrl).build();
    consumer = client.newConsumer()
        .topic("persistent://study/app1/partitionTopic")
        .subscriptionName("sub-4") // 單台會有default，多台狀況下需要設定才共享消費進度
        .subscriptionType(SubscriptionType.Key_Shared)
        .messageListener((c, msg) -> {
          try {
            Map<String, String> properties = msg.getProperties();
            System.out.println("====================>" + c.getConsumerName() + " received: " + new String(msg.getData()));
            System.out.println("====================> properties=" + properties);
            c.acknowledge(msg);
          } catch (Exception e) {
            c.negativeAcknowledge(msg);
          }
        })
        .subscribe();
    System.out.println("Consumer " + consumer.getConsumerName() + " started");
  }

  /**
   * bean被消毀前調用
   */
  @PreDestroy
  public void cleanup() {
    try {
      if (consumer != null) {
        consumer.close();
      }
      if (client != null) {
        client.close();
      }
    } catch (PulsarClientException e) {
      log.error("Failed to close Pulsar client or consumer", e);
    }
  }


}
