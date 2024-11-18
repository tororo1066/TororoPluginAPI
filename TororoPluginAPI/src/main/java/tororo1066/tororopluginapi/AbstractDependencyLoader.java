package tororo1066.tororopluginapi;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractDependencyLoader implements PluginLoader {
    public abstract Library[] getDependencies();

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        for (Library library : getDependencies()) {
            resolver.addRepository(library.getRepository());
            resolver.addDependency(library.getDependency());
        }

        classpathBuilder.addLibrary(resolver);
    }
}
