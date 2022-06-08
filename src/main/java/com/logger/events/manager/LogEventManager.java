package com.logger.events.manager;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logger.events.conf.ApplicationData;
import com.logger.events.domain.LogAlert;
import com.logger.events.model.Context;
import com.logger.events.model.Event;
import com.logger.events.model.State;
import com.logger.events.repository.AlertRepository;

@Component
public class LogEventManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogEventManager.class);

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private ApplicationData applicationData;

    public void parseAndPersistEvents(Context context) {

    	Map<String, Event> eventMap = new HashMap<>();
        Map<String, LogAlert> alerts = new HashMap<>();

        LOGGER.info("Parsing the events and persisting the alerts. This may take a while...");
        try (LineIterator li = FileUtils.lineIterator(new ClassPathResource("logsamples/" + context.getLogFilePath()).getFile())) {
        	
            while (li.hasNext()) {
            	
                Event event;
                try {
                	
                    event = new ObjectMapper().readValue(li.nextLine(), Event.class);
                    LOGGER.trace("{}", event);
                    if (eventMap.containsKey(event.getId())) {
                    	
                        Event e1 = eventMap.get(event.getId());
                        long executionTime = getEventExecutionTime(event, e1);

                        LogAlert alert = new LogAlert(event, Math.toIntExact(executionTime));
                        if (executionTime > applicationData.getAlertThresholdMs()) {
                        	
                            alert.setAlert(Boolean.TRUE);
                            LOGGER.trace("!!! Execution time for the event {} is {}ms", event.getId(), executionTime);
                        }

                        alerts.put(event.getId(), alert);

                        eventMap.remove(event.getId());
                    } else {
                    	
                        eventMap.put(event.getId(), event);
                    }
                } catch (JsonProcessingException e) {
                	
                    LOGGER.error("Unable to parse the event! {}", e.getMessage());
                }

                // to reduce memory consumption, write off the alerts once the pool has enough alerts
                if (alerts.size() > applicationData.getTableRowsWriteoffCount()) {
                    persistAlerts(alerts.values());
                    alerts = new HashMap<>();
                }
            } // END while
            if (alerts.size() != 0) {
                persistAlerts(alerts.values());
            }
        } catch (IOException e) {
            LOGGER.error("!!! Unable to access the file: {}", e.getMessage());
        }
    }

    private void persistAlerts(Collection<LogAlert> alerts) {
    	
        LOGGER.debug("Persisting {} alerts...", alerts.size());
        alertRepository.saveAll(alerts);
    }

    private long getEventExecutionTime(Event event1, Event event2) {
    	
        Event endEvent = Stream.of(event1, event2).filter(e -> State.FINISHED.equals(e.getState())).findFirst().orElse(null);
        Event startEvent = Stream.of(event1, event2).filter(e -> State.STARTED.equals(e.getState())).findFirst().orElse(null);
        return Objects.requireNonNull(endEvent).getTimestamp() - Objects.requireNonNull(startEvent).getTimestamp();
    }
    
}
