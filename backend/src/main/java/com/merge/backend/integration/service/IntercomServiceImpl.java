package com.merge.backend.integration.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IntercomServiceImpl implements IntercomService {

    private static final Logger log = LoggerFactory.getLogger(IntercomServiceImpl.class);

    @Override
    public void sendReachOutMessage(String studentEmail, String message) {
        log.info("[Intercom] Sending reach-out message to {}: {}", studentEmail, message);
    }
}
