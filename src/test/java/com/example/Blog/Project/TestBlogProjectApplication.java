package com.example.Blog.Project;

import org.springframework.boot.SpringApplication;

public class TestBlogProjectApplication {

	public static void main(String[] args) {
		SpringApplication.from(BlogProjectApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
