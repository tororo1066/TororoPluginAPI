package tororo1066.tororopluginapi.annotation

/**
 * これを付けた変数はコマンドとして登録される
 *
 * 変数のクラスがSCommandを親クラスとして指定されている場合のみ動く
 * ```java
 * //例 Java
 * @SCommandBody
 * SCommandObject object = command().addArgs(...).setNormalExecutor(...);
 * ```
 * ```kotlin
 * //例 Kotlin
 * @SCommandBody
 * val object = command().addArgs(...).setNormalExecutor(...)
 * ```
 * @param permission コマンド実行権限
 */
@Target(AnnotationTarget.FIELD)
annotation class SCommandBody(val permission: String = "")
