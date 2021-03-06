/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.proxy.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.stat.JdbcSqlStat;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class StatementProxyImpl extends WrapperProxyImpl implements StatementProxy {

    private final ConnectionProxy  connection;
    private final Statement        statement;

    protected String               lastExecuteSql;
    private long                   lastExecuteStartNano;
    private long                   lastExecuteTimeNano;

    protected JdbcSqlStat          sqlStat;
    protected boolean              firstResultSet;

    protected ArrayList<String>    batchSqlList;

    protected StatementExecuteType lastExecuteType;

    protected Integer              updateCount = null;

    public StatementProxyImpl(ConnectionProxy connection, Statement statement, long id){
        super(statement, id);
        this.connection = connection;
        this.statement = statement;
    }

    public ConnectionProxy getConnectionProxy() {
        return connection;
    }

    public Statement getRawObject() {
        return this.statement;
    }

    public FilterChain createChain() {
        return new FilterChainImpl(this.connection.getDirectDataSource());
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        if (batchSqlList == null) {
            batchSqlList = new ArrayList<String>();
        }

        createChain().statement_addBatch(this, sql);
        batchSqlList.add(sql);
    }

    @Override
    public void cancel() throws SQLException {
        createChain().statement_cancel(this);
    }

    @Override
    public void clearBatch() throws SQLException {
        if (batchSqlList == null) {
            batchSqlList = new ArrayList<String>();
        }

        createChain().statement_clearBatch(this);
        batchSqlList.clear();
    }

    @Override
    public void clearWarnings() throws SQLException {
        createChain().statement_clearWarnings(this);
    }

    @Override
    public void close() throws SQLException {
        createChain().statement_close(this);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        updateCount = null;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.Execute;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;
        
        firstResultSet = createChain().statement_execute(this, sql);
        return firstResultSet;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        updateCount = null;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.Execute;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;
        
        firstResultSet = createChain().statement_execute(this, sql, autoGeneratedKeys);
        return firstResultSet;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        updateCount = null;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.Execute;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;
        
        firstResultSet = createChain().statement_execute(this, sql, columnIndexes);
        return firstResultSet;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        updateCount = null;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.Execute;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;
        
        firstResultSet = createChain().statement_execute(this, sql, columnNames);
        return firstResultSet;
    }

    @Override
    public int[] executeBatch() throws SQLException {
        firstResultSet = false;
        lastExecuteType = StatementExecuteType.ExecuteBatch;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;
        
        int[] updateCounts = createChain().statement_executeBatch(this);

        if (updateCounts != null && updateCounts.length == 1) {
            updateCount = updateCounts[0];
        }

        return updateCounts;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        firstResultSet = true;
        updateCount = null;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.ExecuteQuery;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;
        
        return createChain().statement_executeQuery(this, sql);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        firstResultSet = false;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.ExecuteUpdate;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;
        
        updateCount = createChain().statement_executeUpdate(this, sql);
        return updateCount;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        firstResultSet = false;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.ExecuteUpdate;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;
        
        updateCount = createChain().statement_executeUpdate(this, sql, autoGeneratedKeys);
        return updateCount;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        firstResultSet = false;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.ExecuteUpdate;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;
        
        updateCount = createChain().statement_executeUpdate(this, sql, columnIndexes);
        return updateCount;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        firstResultSet = false;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.ExecuteUpdate;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;
        
        updateCount = createChain().statement_executeUpdate(this, sql, columnNames);
        return updateCount;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return createChain().statement_getConnection(this);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return createChain().statement_getFetchDirection(this);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return createChain().statement_getFetchSize(this);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return createChain().statement_getGeneratedKeys(this);
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return createChain().statement_getMaxFieldSize(this);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return createChain().statement_getMaxRows(this);
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        updateCount = null;
        return createChain().statement_getMoreResults(this);
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        updateCount = null;
        return createChain().statement_getMoreResults(this, current);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return createChain().statement_getQueryTimeout(this);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return createChain().statement_getResultSet(this);
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return createChain().statement_getResultSetConcurrency(this);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return createChain().statement_getResultSetHoldability(this);
    }

    @Override
    public int getResultSetType() throws SQLException {
        return createChain().statement_getResultSetType(this);
    }

    // bug fixed for oracle
    @Override
    public int getUpdateCount() throws SQLException {
        if (updateCount == null) {
            updateCount = createChain().statement_getUpdateCount(this);
        }
        return updateCount;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return createChain().statement_getWarnings(this);
    }

    @Override
    public boolean isClosed() throws SQLException {
        return createChain().statement_isClosed(this);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return createChain().statement_isPoolable(this);
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        createChain().statement_setCursorName(this, name);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        createChain().statement_setEscapeProcessing(this, enable);
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        createChain().statement_setFetchDirection(this, direction);
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        createChain().statement_setFetchSize(this, rows);
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        createChain().statement_setMaxFieldSize(this, max);
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        createChain().statement_setMaxRows(this, max);
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        createChain().statement_setPoolable(this, poolable);
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        createChain().statement_setQueryTimeout(this, seconds);
    }

    @Override
    public List<String> getBatchSqlList() {
        if (batchSqlList == null) {
            batchSqlList = new ArrayList<String>();
        }

        return batchSqlList;
    }

    @Override
    public String getBatchSql() {
        List<String> sqlList = getBatchSqlList();
        StringBuffer buf = new StringBuffer();
        for (String item : sqlList) {
            if (buf.length() > 0) {
                buf.append("\n;\n");
            }
            buf.append(item);
        }
        return buf.toString();
    }

    public String getLastExecuteSql() {
        return lastExecuteSql;
    }

    public void closeOnCompletion() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public boolean isCloseOnCompletion() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == Statement.class) {
            return (T) statement;
        }

        return super.unwrap(iface);
    }

    @Override
    public Map<Integer, JdbcParameter> getParameters() {
        return Collections.emptyMap();
    }

    public JdbcSqlStat getSqlStat() {
        return sqlStat;
    }

    public void setSqlStat(JdbcSqlStat sqlStat) {
        this.sqlStat = sqlStat;
    }

    public long getLastExecuteTimeNano() {
        return lastExecuteTimeNano;
    }

    public void setLastExecuteTimeNano(long lastExecuteTimeNano) {
        this.lastExecuteTimeNano = lastExecuteTimeNano;
    }
    
    public void setLastExecuteTimeNano() {
        if (this.lastExecuteTimeNano <= 0 && this.lastExecuteStartNano > 0) {
            this.lastExecuteTimeNano = System.nanoTime() - this.lastExecuteStartNano;
        }
    }

    public long getLastExecuteStartNano() {
        return lastExecuteStartNano;
    }

    public void setLastExecuteStartNano(long lastExecuteStartNano) {
        this.lastExecuteStartNano = lastExecuteStartNano;
        this.lastExecuteTimeNano = -1L;
    }

    public void setLastExecuteStartNano() {
        if (lastExecuteStartNano <= 0) {
            setLastExecuteStartNano(System.nanoTime());
        }
    }

    public StatementExecuteType getLastExecuteType() {
        return lastExecuteType;
    }

    public boolean isFirstResultSet() {
        return firstResultSet;
    }
}
