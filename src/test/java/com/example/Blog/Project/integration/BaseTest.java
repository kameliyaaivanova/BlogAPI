package com.example.Blog.Project.integration;

import com.example.Blog.Project.integration.data.Seeder;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class BaseTest {

    @Autowired
    protected Seeder seeder;

    @BeforeEach
    public void clean() {
        seeder.clearData();
    }
}
