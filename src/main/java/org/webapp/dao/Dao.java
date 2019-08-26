package org.webapp.dao;

import java.util.List;

public interface Dao<T> {
    void save(T model);

    List<T> findByParam(String column, Object parameter);

    void delete(String column, Object parameter);

    List<T> findAll();
}
