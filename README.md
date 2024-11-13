# TororoPluginAPI
依存関係追加方法

```gradle
repositories {
   maven {
        url = uri('https://maven.pkg.github.com/tororo1066/TororoPluginAPI')
        credentials {
            username = <GITHUB_USERNAME>
            password = <GITHUB_TOKEN>
        }
    }
}
```
```gradle
dependencies {
    implementation 'tororo1066:tororopluginapi::<Version>'
}
```
