package enums;

public enum ReportType {

    DEFAULT(""),
    CSV("csv"),
    PDF("pdf");

    private String fileFormat;

    ReportType(String type) {
        this.fileFormat = type;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }
}
