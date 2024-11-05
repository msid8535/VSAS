public class ScrollFilter {
    private String name;
    private String uploaderId;
    private String fileType;
    private String startDate;
    private String endDate;
    private Integer minDownloads;
    private String orderBy;
    private boolean descending;

    // Constructor
    public ScrollFilter() {
        this.descending = false;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getUploaderId() { return uploaderId; }
    public void setUploaderId(String uploaderId) { this.uploaderId = uploaderId; }
    
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    
    public Integer getMinDownloads() { return minDownloads; }
    public void setMinDownloads(Integer minDownloads) { this.minDownloads = minDownloads; }
    
    public String getOrderBy() { return orderBy; }
    public void setOrderBy(String orderBy) { this.orderBy = orderBy; }
    
    public boolean isDescending() { return descending; }
    public void setDescending(boolean descending) { this.descending = descending; }
}