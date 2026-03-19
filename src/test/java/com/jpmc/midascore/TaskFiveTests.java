package com.jpmc.midascore;

import com.jpmc.midascore.component.TransactionService;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.entity.User;
import com.jpmc.midascore.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TaskFiveTests {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserPopulator userPopulator;
    @Autowired
    private FileLoader fileLoader;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void task_five_verifier() throws Exception {
        userPopulator.populate();
        String[] fileNames = {"poiuytrewq.uiop", "mnbvcxz.vbnm", "lkjhgfdsa.hjkl", "alskdjfh.fhdjsk", "rueiwoqp.tyruei"};
        
        for (String fileName : fileNames) {
            String[] lines = fileLoader.loadStrings("/test_data/" + fileName);
            for (String line : lines) {
                try {
                    String[] data = line.split(", ");
                    if (data.length >= 3) {
                        Transaction tx = new Transaction(Long.parseLong(data[0]), Long.parseLong(data[1]), Float.parseFloat(data[2]));
                        transactionService.processTransaction(tx);
                    }
                } catch (Exception e) {
                    // Skips headers or non-transaction lines like "bernie"
                }
            }
        }

        System.out.println("--- BEGIN TASK FIVE ---");
        userRepository.findAll().forEach(user -> {
            String response = restTemplate.getForObject("http://localhost:33400/balance?userId=" + user.getId(), String.class);
            System.out.println(response);
        });
        System.out.println("--- END TASK FIVE ---");
    }
}
