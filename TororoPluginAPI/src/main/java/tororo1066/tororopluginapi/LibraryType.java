package tororo1066.tororopluginapi;

public enum LibraryType {

    KOTLIN("org.jetbrains.kotlin:kotlin-stdlib", "1.7.20", Repository.MAVEN_CENTRAL),
    KOTLIN_JDK8("org.jetbrains.kotlin:kotlin-stdlib-jdk8", "1.7.20", Repository.MAVEN_CENTRAL),
    MONGODB("org.mongodb:mongodb-driver-sync", "4.11.1", Repository.MAVEN_CENTRAL),
    EVALEX("com.ezylang:EvalEx", "3.1.2", Repository.MAVEN_CENTRAL),;

    public final String name;
    public final String defaultVersion;
    public final String repository;

    LibraryType(String name, String defaultVersion, String repository) {
        this.name = name;
        this.defaultVersion = defaultVersion;
        this.repository = repository;
    }

    LibraryType(String name, String defaultVersion, Repository repository) {
        this(name, defaultVersion, repository.url);
    }

    public Library createLibrary() {
        return new Library(this);
    }

    public Library createLibrary(String version) {
        return new Library(this, version);
    }

    public Library createLibrary(String version, String scope) {
        return new Library(this, version, scope);
    }
}
