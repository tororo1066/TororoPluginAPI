package tororo1066.tororopluginapi;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.jetbrains.annotations.NotNull;

abstract class AbstractKotlinResolver implements PluginLoader {

    protected final Class<? extends JavaPlugin> clazz;
    protected final String version;

    public AbstractKotlinResolver(Class<? extends JavaPlugin> clazz, String version) {
        this.clazz = clazz;
        this.version = version;
    }

    @Override
    public void classloader(@NotNull PluginClasspathBuilder builder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
//        resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.kotlin:kotlin-stdlib:" + version), null));
        try {
            clazz.getClassLoader().loadClass("kotlin.jvm.internal.Intrinsics");
        } catch (ClassNotFoundException e) {
            resolver.addDependency(new Dependency(new DefaultArtifact("org.jetbrains.kotlin:kotlin-stdlib:" + version), "compile"));
        }
        builder.addLibrary(resolver);
    }
}
