package com.jpmc.midascore;

import com.jpmc.midascore.entity.User;
import com.jpmc.midascore.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class TaskFourTests {
    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private UserPopulator userPopulator;
    @Autowired
    private FileLoader fileLoader;
    @Autowired
    private UserRepository userRepository;

    @Test
    void task_four_verifier() throws Exception {
        userPopulator.populate();
        String[] transactionLines = fileLoader.loadStrings("/test_data/alskdjfh.fhdjsk");
        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }
        Thread.sleep(10000);

        System.err.println("vvvvvvvvvvvvvvvvvvvv USER LIST START vvvvvvvvvvvvvvvvvvvv");
        userRepository.findAll().forEach(u -> {
            System.err.println("USER_FOUND: " + u.getName() + " | BALANCE: " + u.getBalance());
        });
        System.err.println("^^^^^^^^^^^^^^^^^^^^ USER LIST END ^^^^^^^^^^^^^^^^^^^^");
    }
}
