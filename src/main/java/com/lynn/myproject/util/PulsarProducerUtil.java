package com.lynn.myproject.util;

import com.lynn.myproject.dto.ProducerSendDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @Author: Lynn on 2024/8/16
 */
@Slf4j
@Component
public class PulsarProducerUtil {

  @Value("${PULSAR_ADMIN_URL}")
  private String pulsarAdminUrl;

  public void sendMsg(ProducerSendDto producerSendDto) {
    try (PulsarClient client = PulsarClient.builder().serviceUrl(pulsarAdminUrl).build()) {
      try (Producer<byte[]> producer = client.newProducer()
          .topic(producerSendDto.getTopic())
          .create()) {
        producer.newMessage()
            .key(producerSendDto.getKey())// 具有相同的key會被交給相同的consumer
            .value(producerSendDto.getMsg().getBytes(StandardCharsets.UTF_8))
            .properties(producerSendDto.getPropertyMap())// 添加屬性可以放一些訊息 時間之類的
            .send();
      }
    } catch (PulsarClientException e) {
      log.error(e.getMessage(), e);
    }
  }

  public void sendAtTime(ProducerSendDto producerSendDto) {
    try (PulsarClient client = PulsarClient.builder().serviceUrl(pulsarAdminUrl).build()) {
      try (Producer<byte[]> producer = client.newProducer()
          .topic(producerSendDto.getTopic())
          .create()) {
        producer.newMessage()
            .key(producerSendDto.getKey())
            .deliverAt(producerSendDto.getDeliverAtTimestamp())
            .value(producerSendDto.getMsg().getBytes(StandardCharsets.UTF_8))
            .properties(producerSendDto.getPropertyMap())
            .send();
      }
    } catch (PulsarClientException e) {
      log.error(e.getMessage(), e);
    }
  }

  public void sendAfterTime(ProducerSendDto producerSendDto) {
    try (PulsarClient client = PulsarClient.builder().serviceUrl(pulsarAdminUrl).build()) {
      try (Producer<byte[]> producer = client.newProducer()
          .topic(producerSendDto.getTopic())
          .create()) {
        producer.newMessage()
            .key(producerSendDto.getKey())
            .deliverAfter(producerSendDto.getDeliverAfterTime(), producerSendDto.getTimeUnit())
            .value(producerSendDto.getMsg().getBytes(StandardCharsets.UTF_8))
            .properties(producerSendDto.getPropertyMap())
            .send();
      }
    } catch (PulsarClientException e) {
      log.error(e.getMessage(), e);
    }
  }

}



