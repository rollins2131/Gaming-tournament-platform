package com.tournament.service.analytics;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;

import com.tournament.model.enums.ReportStatus;

@Service
public class ReportLifecycleService {

    private final AtomicReference<ReportStatus> status = new AtomicReference<>(ReportStatus.ARCHIVED);

    public ReportStatus getCurrentStatus() {
        return status.get();
    }

    public ReportStatus generate() {
        status.set(ReportStatus.GENERATED);
        return status.get();
    }

    public ReportStatus publish() {
        if (status.get() != ReportStatus.GENERATED) {
            throw new IllegalStateException("Generate a report before publishing");
        }
        status.set(ReportStatus.PUBLISHED);
        return status.get();
    }

    public ReportStatus archive() {
        if (status.get() == ReportStatus.ARCHIVED) {
            throw new IllegalStateException("Report is already archived");
        }
        status.set(ReportStatus.ARCHIVED);
        return status.get();
    }
}
