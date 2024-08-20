package com.lynn.myproject.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Lynn on 2024/8/20
 */
@Data
@Builder
public class ProducerSendDto {

  private String topic;

  private String key;

  private String msg;

  private Map<String, String> propertyMap;

  private long deliverAtTimestamp;

  private int deliverAfterTime;

  private TimeUnit timeUnit;

}
