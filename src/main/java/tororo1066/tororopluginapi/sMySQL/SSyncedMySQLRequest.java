package tororo1066.tororopluginapi.sMySQL;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SSyncedMySQLRequest {
    public Consumer<Boolean> executeCallback;
    public Consumer<ArrayList<SMySQLResult>> queryCallback;
    public String query;

    public boolean isQuery(){
        return queryCallback != null;
    }
}