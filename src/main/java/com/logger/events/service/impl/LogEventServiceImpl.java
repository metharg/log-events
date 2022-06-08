package com.logger.events.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logger.events.manager.LogEventManager;
import com.logger.events.model.Context;
import com.logger.events.service.LogEventService;

@Service
public class LogEventServiceImpl implements LogEventService {
	
    private static final Logger logger = LoggerFactory.getLogger(LogEventServiceImpl.class);

    @Autowired
    private LogEventManager manager;

    @Override
    public void execute(String... args) {
    	
    	logger.info("execute method");
        Context context = Context.getInstance();
        context.setLogFilePath(args[0]);
        manager.parseAndPersistEvents(context);
    }

}
