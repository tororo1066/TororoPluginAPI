package tororo1066.tororopluginapi;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.jetbrains.annotations.NotNull;

public class DefaultKotlinResolver extends AbstractKotlinResolver {
    public DefaultKotlinResolver(Class<? extends JavaPlugin> clazz, String kotlinVersion) {
        super(clazz, kotlinVersion);
    }

    @Override
    public void classloader(@NotNull PluginClasspathBuilder builder) {
        super.classloader(builder);

        MavenLibraryResolver resolver = new MavenLibraryResolver();
        try {
            clazz.getClassLoader().loadClass("org.mongodb.client.MongoClient");
        } catch (ClassNotFoundException e) {
            resolver.addDependency(new Dependency(new DefaultArtifact("org.mongodb:mongodb-driver-sync:4.11.1"), "compile"));
        }
        try {
            clazz.getClassLoader().loadClass("com.ezylang.evalex.Expression");
        } catch (ClassNotFoundException e) {
            resolver.addDependency(new Dependency(new DefaultArtifact("com.ezylang:EvalEx:3.1.2"), "compile"));
        }
//        resolver.addDependency(new Dependency(new DefaultArtifact("org.mongodb:mongodb-driver-sync:4.11.1"), null));
//        resolver.addDependency(new Dependency(new DefaultArtifact("com.ezylang:EvalEx:3.1.2"), null));
    }
}
