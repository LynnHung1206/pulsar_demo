//package com.lynn.myproject.t;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.pulsar.client.admin.PulsarAdmin;
//import org.apache.pulsar.client.admin.PulsarAdminException;
//import org.apache.pulsar.client.api.Consumer;
//import org.apache.pulsar.client.api.ConsumerBuilder;
//import org.apache.pulsar.client.api.Message;
//import org.apache.pulsar.client.api.Producer;
//import org.apache.pulsar.client.api.PulsarClient;
//import org.apache.pulsar.client.api.PulsarClientException;
//import org.apache.pulsar.client.api.Schema;
//import org.apache.pulsar.client.api.SubscriptionType;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
///**
// * @Author: Lynn on 2024/8/15
// */
//@SpringBootTest
//@Slf4j
//public class PulsarTest {
//
//  @Test
//  public void testCreatePulsarClient() throws PulsarClientException {
//    System.out.println("======> start");
//    try (PulsarClient client = PulsarClient.builder().serviceUrl("pulsar://localhost:6650").build()) {
//      System.out.println(client);
//    }
//    System.out.println("======> end <========");
//  }
//
//  @Test
//  public void testProducer() throws PulsarClientException {
//    try (PulsarClient client = PulsarClient.builder().serviceUrl("pulsar://localhost:6650").build()) {
//      try (Producer<byte[]> producer = client.newProducer()
//          .topic("persistent://study/app1/topic-1")
//          .create()) {
//        // 1
//        producer.send("hello".getBytes(StandardCharsets.UTF_8));
//        // 2
//        producer.newMessage()
//            .key("msgKey1")// 具有相同的key會被交給相同的consumer
//            .value("helloooooooo".getBytes(StandardCharsets.UTF_8))
//            .property("p1", "v1")// 添加屬性可以放一些訊息 時間之類的
//            .property("p2", "v2")
//            .send();
//        // 3
//        try (Producer<String> stringProducer = client.newProducer(Schema.STRING)
//            .topic("topic-1")
//            .create()) {
//          stringProducer.send("hi hi");
//        }
//      }
//    }
//  }
//
//
//  /**
//   * 同步方式較不好
//   *
//   * @throws PulsarClientException
//   */
//  @Test
//  public void testConsumerWhile() throws PulsarClientException {
//    try (PulsarClient client = PulsarClient.builder().serviceUrl("pulsar://localhost:6650").build()) {
//      Consumer<byte[]> consumer = client.newConsumer()
//          .topic("persistent://study/app1/topic-1")
//          .subscriptionName("sub-2")
//          .subscriptionType(SubscriptionType.Exclusive) // 订阅类型: 独占模式
//          .subscribe();
//
//      while (true) {
//        // 等待一个消息
//        Message<byte[]> msg = consumer.receive();
//        try {
//          // 处理消息
//          System.out.println("=====> Message received: " + new String(msg.getData()));
//          // 处理完成发送确认ACK, 通知Broker消息可以被删除
//          consumer.acknowledge(msg);
//        } catch (Exception e) {
//          // 处理失败，发送否定确认(negative ack)，在稍后的时间消息会重新发给消费者进行重试
//          consumer.negativeAcknowledge(msg);
//        }
//        break;
//      }
//    }
//  }
//
//  @Test
//  public void testConsumerMessageListener() throws PulsarClientException, InterruptedException {
//    try (PulsarClient client = PulsarClient.builder().serviceUrl("pulsar://localhost:6650").build()) {
//      ConsumerBuilder<byte[]> consumerBuilder = client.newConsumer()
//          .topic("persistent://study/app1/topic-1")
//          .subscriptionName("sub-4")
//          .subscriptionType(SubscriptionType.Shared) // 订阅类型: 共享模式
//          .messageListener((c, msg) -> {
//            try {
//              System.out.println(c.getConsumerName() + " received: " + new String(msg.getData()));
//              c.acknowledge(msg);
//            } catch (Exception e) {
//              c.negativeAcknowledge(msg);
//            }
//          });
//      for (int i = 0; i < 4; i++) {
//        consumerBuilder.consumerName("testConsumerMessageListener-" + i).subscribe();
//      }
//      Thread.sleep(TimeUnit.MINUTES.toMillis(1));
//    }
//  }
//
//
//  @Test
//  public void isTopicExist() {
//    String topicName = "persistent://study/app1/topic-1";
//    try (PulsarAdmin pulsarAdmin = PulsarAdmin.builder()
//        .serviceHttpUrl("http://localhost:8080")
//        .build()) {
//
//      pulsarAdmin.namespaces()
//          .getTopics("study/app1")
//          .forEach(System.out::println);
//
//      List<String> partitionedTopicList = pulsarAdmin.topics()
//          .getPartitionedTopicList("study/app1");
//      partitionedTopicList.forEach(System.out::println);
//
//      boolean contains = partitionedTopicList
//          .contains(topicName);
//      log.info("=====> contains={}", contains);
//
//    } catch (PulsarClientException | PulsarAdminException e){
//      log.error("create topic failed", e);
//    }
//  }
//
//}
