package tororo1066.tororopluginapi;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.jetbrains.annotations.NotNull;

abstract class AbstractKotlinResolver implements PluginLoader {

    private final String version;

    public AbstractKotlinResolver(String version) {
        this.version = version;
    }

    @Override
    public void classloader(@NotNull PluginClasspathBuilder builder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.kotlin:kotlin-stdlib:" + version), null));

        builder.addLibrary(resolver);
    }
}
