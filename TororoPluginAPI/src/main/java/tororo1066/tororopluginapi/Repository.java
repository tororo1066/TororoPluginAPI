package tororo1066.tororopluginapi;

public enum Repository {
    MAVEN_CENTRAL("https://maven-central.storage-download.googleapis.com/maven2");

    public final String url;

    Repository(String url) {
        this.url = url;
    }
}
