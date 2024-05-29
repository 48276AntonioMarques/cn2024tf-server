package pt.isel.leic.cn.server.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import pt.isel.leic.cn.server.Server;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static pt.isel.leic.cn.server.ExitCode.FAULTY_PUBLISHER;

public class PublishService {

    private final Publisher publisher;

    public PublishService(String projectID, String topicID) {
        TopicName tName = TopicName.ofProjectTopicName(projectID, topicID);
        try {
            this.publisher = Publisher.newBuilder(tName).build();

        } catch (IOException e) {
            e.printStackTrace();
            Server.exit(FAULTY_PUBLISHER);
            throw new RuntimeException(e);
        }
    }

    public void publish(String reference, String language) {
        ByteString msgData = ByteString.copyFromUtf8(reference);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                .setData(msgData)
                .putAttributes("targetLanguage", language)
                .build();
        ApiFuture<String> future = publisher.publish(pubsubMessage);
        try {
            String msgID = future.get();
            System.out.println("Message Published with ID = " + msgID);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        publisher.shutdown();
    }
}
