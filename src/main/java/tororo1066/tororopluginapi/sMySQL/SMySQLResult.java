package tororo1066.tororopluginapi.sMySQL;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class SMySQLResult{

    HashMap<String, Object> result;

    public SMySQLResult(HashMap<String, Object> data){
        this.result = data;
    }

    public String getString(String colName){
        return String.valueOf(result.get(colName));
    }
    public int getInt(String colName){
        Object obj = result.get(colName);
        if(obj instanceof BigDecimal){
            return ((BigDecimal) obj).intValue();
        }
        return (Integer) obj;
    }
    public boolean getBoolean(String colName){
        return Boolean.parseBoolean((String)result.get(colName));
    }
    public double getDouble(String colName){
        return (Double) result.get(colName);
    }
    public long getLong(String colName){
        return (Long) result.get(colName);
    }
    public Date getDate(String colName){
        return (Date) result.get(colName);
    }

    public Object getObject(String colName) {
        return result.get(colName);
    }


    public Set<String> getKeys(){
        return result.keySet();
    }

}