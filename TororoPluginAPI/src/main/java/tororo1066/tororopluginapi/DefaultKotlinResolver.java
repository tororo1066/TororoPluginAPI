package tororo1066.tororopluginapi;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.jetbrains.annotations.NotNull;

public class DefaultKotlinResolver extends AbstractKotlinResolver {
    public DefaultKotlinResolver() {
        super("1.7.20");
    }

    @Override
    public void classloader(@NotNull PluginClasspathBuilder builder) {
        super.classloader(builder);

        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addDependency(new Dependency(new DefaultArtifact("org.mongodb:mongodb-driver-sync:4.11.1"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("com.ezylang:EvalEx:3.1.2"), null));
    }
}
