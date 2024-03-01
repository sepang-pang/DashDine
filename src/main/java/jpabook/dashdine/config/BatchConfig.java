package jpabook.dashdine.config;

import jpabook.dashdine.batch.QueueItemReader;
import jpabook.dashdine.domain.user.User;
import jpabook.dashdine.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final PlatformTransactionManager transactionManager;
    private final UserRepository userRepository;

    @Bean
    public Job job(JobRepository jobRepository) {
        return new JobBuilder("myFirstJob", jobRepository)
                .start(step(jobRepository))
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository) {
        return new StepBuilder("myFirstStep", jobRepository)
                .<User, User>chunk(10, transactionManager)
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    @StepScope
    public QueueItemReader<User> itemReader() {
        List<User> usersToDelete = userRepository.findUsersToDelete(LocalDateTime.now().minusDays(5));
        return new QueueItemReader<>(usersToDelete);
    }

    public ItemWriter<User> itemWriter() {
        return userRepository::deleteAll;
    }
}

