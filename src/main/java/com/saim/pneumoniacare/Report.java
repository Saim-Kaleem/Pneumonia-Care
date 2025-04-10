package com.saim.pneumoniacare;

public class Report {
    private int reportId;
    private int userId;
    private String report;

    public Report(int reportId, int userId, String report) {
        this.reportId = reportId;
        this.userId = userId;
        this.report = report;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}
