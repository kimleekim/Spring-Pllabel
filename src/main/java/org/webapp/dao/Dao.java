package org.webapp.dao;

import java.util.List;
import java.util.Map;

public interface Dao<T> {
    void save(T model);

    List<T> findByParam(Map<String, Object> parameter);

    void delete(Map<String, Object> parameter);

    List<T> findAll();
}
