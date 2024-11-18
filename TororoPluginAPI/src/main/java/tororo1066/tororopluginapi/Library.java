package tororo1066.tororopluginapi;

import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

public class Library {
    String name;
    String version;
    String url;
    String scope = "compile";

    public Library(LibraryType type, String version, String scope) {
        this.name = type.name;
        this.version = version;
        this.url = type.repository;
        this.scope = scope;
    }

    public Library(LibraryType type) {
        this(type, type.defaultVersion , "compile");
    }

    public Library(LibraryType type, String version) {
        this(type, version, "compile");
    }

    public Library(String name, String version, String url, String scope) {
        this.name = name;
        this.version = version;
        this.url = url;
        this.scope = scope;
    }

    public RemoteRepository getRepository() {
        return new RemoteRepository.Builder(name, "default", url).build();
    }

    public Dependency getDependency() {
        return new Dependency(new DefaultArtifact(name + ":" + version), scope);
    }
}
