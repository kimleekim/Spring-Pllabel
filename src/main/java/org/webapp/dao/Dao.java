package org.webapp.dao;

import java.util.List;

public interface Dao<T> {
    void save(T model);

    T findByParam(Object parameter);

    void delete(Object parameter);

    void update(T model);

    List<T> findAll();
}
