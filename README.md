# TororoPluginAPI

Paper プラグイン開発を効率化するための API セットです。  
`SJavaPlugin` を中心に、コマンド登録・イベント登録・各種ユーティリティをまとめて扱えます。

## 何ができるか

- `SJavaPlugin` を継承して、プラグインの初期化/終了処理をシンプルに書ける
- `@SEventHandler` でイベントを自動登録できる（`registerEvents` の記述を減らせる）
- `SCommandV2` ベースでコマンド定義を整理できる
- 必要に応じて `SConfig` / `SMySQL` / `SVault` / 入力補助などをオプション利用できる

## モジュール概要

このリポジトリは複数モジュールで構成されています。

- `TororoPluginAPI`: メイン API（`SJavaPlugin` など）
- `CommandAPI`: コマンド関連の基盤
- `NMSUtils`: バージョン別 NMS ラッパー群

通常の利用はまず `TororoPluginAPI` を導入すれば OK です。

## 導入方法（Gradle）

GitHub Packages から取得します。認証が必要です。

### 1. リポジトリを追加

#### Groovy DSL (`build.gradle`)

```groovy
repositories {
    maven {
        url = uri('https://maven.pkg.github.com/tororo1066/TororoPluginAPI')
        credentials {
            username = findProperty('gpr.user') ?: System.getenv('GITHUB_USERNAME')
            password = findProperty('gpr.key') ?: System.getenv('GITHUB_TOKEN')
        }
    }
}
```

#### Kotlin DSL (`build.gradle.kts`)

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/tororo1066/TororoPluginAPI")
        credentials {
            username = findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
            password = findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
```

### 2. 依存関係を追加

#### Groovy DSL (`build.gradle`)

```groovy
dependencies {
    implementation 'tororo1066:tororopluginapi:<Version>'
}
```

#### Kotlin DSL (`build.gradle.kts`)

```kotlin
dependencies {
    implementation("tororo1066:tororopluginapi:<Version>")
}
```

`<Version>` は利用したい公開バージョンに置き換えてください。

### 3. ビルド時の注意（依存同梱）

この API を使ったプラグインを配布する場合は、依存関係を配布用 jar に同梱してビルドすることを推奨します（例: `shadowJar`）。

#### Groovy DSL (`build.gradle`)

```groovy
plugins {
    id 'com.gradleup.shadow' version '<shadow_version>'
}

tasks.named('build') {
    dependsOn tasks.named('shadowJar')
}
```

#### Kotlin DSL (`build.gradle.kts`)

```kotlin
plugins {
    id("com.gradleup.shadow") version "<shadow_version>"
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}
```

## 最小利用例

```kotlin
import org.bukkit.event.player.PlayerJoinEvent
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.annotation.SEventHandler

class ExamplePlugin : SJavaPlugin(UseOption.SConfig) {
    override fun onStart() {
        logger.info("ExamplePlugin enabled")
    }

    override fun onEnd() {
        logger.info("ExamplePlugin disabled")
    }

    @SEventHandler
    fun onJoin(e: PlayerJoinEvent) {
        e.player.sendMessage("Welcome!")
    }
}
```

## 補足

- この API は Paper 系サーバー向けの利用を前提にしています
- バージョン別 NMS 実装（`NMSUtils`）を含むため、サーバーバージョン差分には注意してください
- GitHub Packages を使うため、`GITHUB_TOKEN` には package 読み取り権限が必要です

