package com.revcart.service;

import java.util.Map;

public interface ActivityLogService {
    void log(Long userId, String action, Map<String, Object> metadata);
}

