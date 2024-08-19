package com.lynn.myproject.controller;

import com.lynn.myproject.util.PulsarProducerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

/**
 * @Author: Lynn on 2024/8/16
 */
@RestController
@RequestMapping(value = "/test")
@RequiredArgsConstructor
public class PulsarController {

  private final PulsarProducerUtil pulsarProducerUtil;

  @RequestMapping(value = "/send/{token}", method = RequestMethod.GET)
  public void sendMessage(@PathVariable("token") String token) {
    Map<String, String> currentTimeStamp = Map.of("currentTimeStamp", String.valueOf(Timestamp.from(Instant.now())), "token", token);
    pulsarProducerUtil.sendMsg("persistent://study/app1/partitionTopic", token, "bellooooooooo!!", currentTimeStamp);
  }
}
