package tororo1066.tororopluginapi;

public enum Repository {
    MAVEN_CENTRAL("https://repo.maven.apache.org/maven2/");

    public final String url;

    Repository(String url) {
        this.url = url;
    }
}
