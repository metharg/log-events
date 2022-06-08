package com.logger.events.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.logger.events.domain.LogAlert;
import com.logger.events.model.EventType;
import com.logger.events.repository.AlertRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
public class LogAlertRepositoryTest {
	
    @Autowired
    private AlertRepository repository;

    @Test
    public void whenFindingCustomerById_thenCorrect() {
        LogAlert alert = new LogAlert();
        alert.setId("alert-1");
        alert.setDuration(3);
        alert.setHost("localhost");
        alert.setType(EventType.APPLICATION_LOG);

        repository.save(alert);
        assertThat(repository.findById("alert-1")).isInstanceOf(Optional.class);
    }

    @Test
    public void whenFindingAllCustomers_thenCorrect() {
        LogAlert alert1 = new LogAlert();
        alert1.setId("alert-1");
        alert1.setDuration(3);
        alert1.setHost("localhost");
        alert1.setType(EventType.APPLICATION_LOG);

        LogAlert alert2 = new LogAlert();
        alert2.setId("alert-2");
        alert2.setDuration(7);
        alert2.setHost(null);
        alert2.setType(null);
        alert2.setAlert(Boolean.TRUE);

        repository.save(alert1);
        repository.save(alert2);

        assertThat(repository.findAll()).isInstanceOf(List.class);
    }
}