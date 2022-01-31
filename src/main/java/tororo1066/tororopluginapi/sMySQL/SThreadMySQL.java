package tororo1066.tororopluginapi.sMySQL;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class SThreadMySQL {

    ExecutorService threadPool = Executors.newCachedThreadPool();

    //synced query
    LinkedBlockingQueue<SSyncedMySQLRequest> syncedQueue = new LinkedBlockingQueue<>();
    Thread syncedThread = new Thread(() -> {
        while(true){
            try {
                SSyncedMySQLRequest request = syncedQueue.take();
                if(request.isQuery()){
                    request.queryCallback.accept(query(request.query));
                }else{
                    request.executeCallback.accept(execute(request.query));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    });

    JavaPlugin plugin;
    public SThreadMySQL(JavaPlugin plugin){
        this.plugin = plugin;
        if(!syncedThread.isAlive()){
            syncedThread.start();
        }
    }

    public Future<ArrayList<SMySQLResult>> futureQuery(String query){
        return threadPool.submit(()-> new SMySQL(plugin).cachedQuery(query));
    }

    public Future<Boolean> futureExecute(String query){
        return threadPool.submit(()-> new SMySQL(plugin).execute(query));
    }

    public ArrayList<SMySQLResult> query(String query){
        try {
            return futureQuery(query).get();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean execute(String query){
        try {
            return futureExecute(query).get();
        } catch (Exception e) {
            return false;
        }
    }

    public void asyncQuery(String query, Consumer<ArrayList<SMySQLResult>> callback){
        threadPool.submit(() -> callback.accept(query(query)));
    }

    public void asyncExecute(String query, Consumer<Boolean> callback){
        threadPool.submit(() -> callback.accept(execute(query)));
    }

    public void syncedFutureQuery(String query, Consumer<ArrayList<SMySQLResult>> callback){
        SSyncedMySQLRequest request = new SSyncedMySQLRequest();
        request.queryCallback = callback;
        request.query = query;
        syncedQueue.add(request);
    }

    public void syncedFutureExecute(String query, Consumer<Boolean> callback){
        SSyncedMySQLRequest request = new SSyncedMySQLRequest();
        request.executeCallback = callback;
        request.query = query;
        syncedQueue.add(request);
    }


}