package org.webapp.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Dao<T> {

    abstract void save(T model);

    protected String addCondition(String type, Map<String, Object> parameter, Iterator<String> keys) {
        String key = "";
        String condition = "";

        if(keys.hasNext()) {
            key = keys.next();

            if(parameter.get(key) instanceof Integer || parameter.get(key) instanceof Long) {
                condition += type + "." + key + "=" + parameter.get(key);
            }
            else
                condition += type + "." + key + "= \"" + parameter.get(key) + "\"";
        }

        return condition;
    }

    protected String selectTarget(String table, Map<String, Object> parameter, String sql) {
        List<T> result;
        Set<String> keyset = parameter.keySet();
        Iterator<String> keys = keyset.iterator();
        int loop = keyset.size();

        for(int i=0; i<keyset.size()-1; i++) {
            sql += (addCondition(table, parameter, keys) + " ");
            sql += "AND ";
        }

        sql += addCondition(table, parameter, keys);

        return sql;
    }

    abstract List<T> findByParam(Map<String, Object> parameter);

    abstract void delete(Map<String, Object> parameter);

    abstract List<T> findAll();

}
