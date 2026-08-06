package com.example.journalsystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class JournalsystemApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void genereraLosenord() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("receptionist"); // Skriv ditt lösenord här

        System.out.println("=========================================");
        System.out.println("DITT KRYPTERADE LÖSENORD ÄR:");
        System.out.println(hash);
        System.out.println("=========================================");
    }

}
