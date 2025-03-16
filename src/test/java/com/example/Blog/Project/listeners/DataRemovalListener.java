package com.example.Blog.Project.listeners;

import com.example.Blog.Project.category.repository.CategoryRepository;
import com.example.Blog.Project.file.repository.FileRepository;
import com.example.Blog.Project.permission.repository.PermissionRepository;
import com.example.Blog.Project.post.repository.PostRepository;
import com.example.Blog.Project.role.repository.RoleRepository;
import com.example.Blog.Project.user.repository.UserRepository;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.util.Arrays;
import java.util.List;

public class DataRemovalListener implements TestExecutionListener {

    @Override
    public void beforeTestMethod(TestContext testContext) {
        AutowireCapableBeanFactory beanFactory = testContext.getApplicationContext().getAutowireCapableBeanFactory();

        List<Class<? extends CrudRepository<?, ?>>> repositoryClasses = Arrays.asList(
                PostRepository.class,
                CategoryRepository.class,
                FileRepository.class,
                UserRepository.class,
                RoleRepository.class,
                PermissionRepository.class
        );

        for (Class<? extends CrudRepository<?, ?>> crudRepositoryClass : repositoryClasses) {
            beanFactory.resolveNamedBean(crudRepositoryClass).getBeanInstance().deleteAll();
        }
    }
}
