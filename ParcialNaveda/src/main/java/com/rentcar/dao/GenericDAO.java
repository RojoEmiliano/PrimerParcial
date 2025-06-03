package com.rentcar.dao;

import java.util.List;
import java.sql.SQLException;

public interface GenericDAO<T> {
    T create(T entity) throws SQLException;
    T getById(int id) throws SQLException;
    List<T> getAll() throws SQLException;
    boolean update(T entity) throws SQLException;
    boolean delete(int id) throws SQLException;
}