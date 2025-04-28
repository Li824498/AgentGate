package com.mylearn.agentgate.core.listener;

import com.mylearn.agentgate.core.domain.history.GeminiHistoryManager;
import com.mylearn.agentgate.core.entity.LResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMQListener {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private GeminiHistoryManager historyManager;


    @RabbitListener(queues = "history.stream.queue")
    public void StreamHistoryListener(HistoryMqMessage historyMqMessage) {
        log.info("mq接收到了消息：" + historyMqMessage.toString());

        String text = stringRedisTemplate.opsForValue().getAndDelete(historyMqMessage.getBucketName());
        LResponse lResponse = new LResponse();
        lResponse.setInContext(text);
        lResponse.setUserId(historyMqMessage.getLRequest().getUserId());
        lResponse.setChatId(historyMqMessage.getLRequest().getChatId());
        lResponse.setMsgIndex(historyMqMessage.getLRequest().getMsgIndex() + 1);

        historyManager.processAfter(lResponse);
    }
}
