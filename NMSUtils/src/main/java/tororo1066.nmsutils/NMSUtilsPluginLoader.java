package tororo1066.nmsutils;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

public class NMSUtilsPluginLoader implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder builder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        resolver.addRepository(new RemoteRepository.Builder("mavenCentral", "default", "https://maven-central.storage-download.googleapis.com/maven2").build());
        resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.2.10"), "compile"));
        builder.addLibrary(resolver);
    }
}
