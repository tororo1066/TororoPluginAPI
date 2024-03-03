package tororo1066.tororopluginapi.annotation

import org.bukkit.event.EventPriority
import tororo1066.tororopluginapi.frombukkit.SBukkit

/**
 * これを付けた関数はEventHandlerとして登録される
 *
 * server.pluginManager.registerEvents()が必要ない
 * ```java
 * //例 Java
 * @SEventHandler
 * public void event(PlayerJoinEvent e){
 *   code...
 * }
 * ```
 * ```kotlin
 * //例 Kotlin
 * @SEventHandler
 * fun event(e: PlayerJoinEvent){
 *   code...
 * }
 * ```
 *
 * @param priority イベントの優先度 LOWEST->LOW->NORMAL->HIGH->HIGHEST->MONITORの順に呼び出される
 * @param autoRegister 自動でイベントを登録するか(デフォルト:true) falseな場合、[SBukkit.registerSEvent][SBukkit.registerSEvent]で登録できる
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SEventHandler(val priority: EventPriority = EventPriority.NORMAL, val autoRegister: Boolean = true)
