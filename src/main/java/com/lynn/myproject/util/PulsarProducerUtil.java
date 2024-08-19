package com.lynn.myproject.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

/**
 * @Author: Lynn on 2024/8/16
 */
@Slf4j
@Component
public class PulsarProducerUtil {

  @Value("${PULSAR_ADMIN_URL}")
  private String pulsarAdminUrl;

  public void sendMsg(String topic, String key, String msg, Map<String,String> propertyMap) {
    try (PulsarClient client = PulsarClient.builder().serviceUrl(pulsarAdminUrl).build()) {
      try (Producer<byte[]> producer = client.newProducer()
          .topic(topic)
          .create()) {
        producer.newMessage()
            .key(key)// 具有相同的key會被交給相同的consumer
            .value(msg.getBytes(StandardCharsets.UTF_8))
            .properties(propertyMap)// 添加屬性可以放一些訊息 時間之類的
            .send();
      }
    }catch (PulsarClientException e){
      log.error(e.getMessage(), e);
    }
  }

}
