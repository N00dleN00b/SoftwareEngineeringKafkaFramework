package com.jpmc.midascore;

import com.jpmc.midascore.entity.User;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Component
public class UserPopulator {
    @Autowired
    private UserRepository userRepository;

    public void populate() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/users.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                User user = new User(data[0], Float.parseFloat(data[1]));
                userRepository.save(user);
            }
        }
    }
}
