package com.revcart.service.impl;

import com.revcart.document.ActivityHistory;
import com.revcart.repository.mongo.ActivityHistoryRepository;
import com.revcart.service.ActivityLogService;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityHistoryRepository repository;

    public ActivityLogServiceImpl(ActivityHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void log(Long userId, String action, Map<String, Object> metadata) {
        ActivityHistory history = new ActivityHistory();
        history.setUserId(userId);
        history.setAction(action);
        history.setMetadata(metadata);
        repository.save(history);
    }
}

