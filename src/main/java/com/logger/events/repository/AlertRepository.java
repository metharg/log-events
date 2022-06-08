package com.logger.events.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.logger.events.domain.LogAlert;

@Repository
public interface AlertRepository extends CrudRepository<LogAlert, String> {
}
