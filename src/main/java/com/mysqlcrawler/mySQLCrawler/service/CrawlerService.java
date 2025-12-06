package com.mysqlcrawler.mySQLCrawler.service;

import com.mysqlcrawler.mySQLCrawler.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class CrawlerService {


    @Autowired
    private DataSource dataSource;


    //To get all tables from database

    public List<String> listTables(CrawlerConfig crawlerConfig) throws Exception{
        List<String> tables = new ArrayList<>();

        String url = crawlerConfig.getDatabase().getJdbcUrl();
        String username = crawlerConfig.getDatabase().getUsername();
        String password = crawlerConfig.getDatabase().getPassword();

        System.out.println("url : " + url + "username : " + username + "password : " + password);

        try(Connection conn = DriverManager.getConnection(url, username, password)) {
            DatabaseMetaData metaData = conn.getMetaData();
//            System.out.println("catalog : " + conn.getCatalog());

            boolean includeViews = crawlerConfig.getCrawler().isIncludeViews();

            String[] types = includeViews ? new String[]{"TABLE", "VIEWS"} : new String[]{"TABLE"};

            try(ResultSet rs = metaData.getTables(conn.getCatalog(), null, "%", types)) {
                while(rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    String tableType = rs.getString("TABLE_TYPE");

                    tables.add(tableName + " (" + tableType + ")");
//                    String tableCatalog = rs.getString("TABLE_CAT");
//                    String tableSchema = rs.getString(("TABLE_SCHEM"));
//                    System.out.println("tableCatalog : " + tableCatalog);
//                    System.out.println("tableSchema : " + tableSchema);
                }
            }
        }
        return tables;
    }

    //To get columns of each table

    public List<ColumnModel> getColumns(String tableName, CrawlerConfig crawlerConfig) throws Exception {

        String url = crawlerConfig.getDatabase().getJdbcUrl();
        String username = crawlerConfig.getDatabase().getUsername();
        String password = crawlerConfig.getDatabase().getPassword();

        List<ColumnModel> columns = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection(url, username, password)) {
            DatabaseMetaData metaData = conn.getMetaData();

            try(ResultSet rs = metaData.getColumns(conn.getCatalog(), null, tableName, "%")) {

//                getResultSetMetaData(rs);
//                System.out.println("\nColumns :\n");
                while(rs.next()) {
                    ColumnModel column = new ColumnModel();

                    column.setName(rs.getString("COLUMN_NAME"));
                    column.setType(rs.getString("TYPE_NAME"));
                    column.setSize(rs.getInt("COLUMN_SIZE"));
                    column.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                    column.setAutoIncrement(rs.getString("IS_AUTOINCREMENT").equals("YES"));

                    columns.add(column);
//                    String columnName = rs.getString("COLUMN_NAME");
//                    String dataType = rs.getString("TYPE_NAME");
//                    int size = rs.getInt("COLUMN_SIZE");
//                    String nullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable ? "YES" : "NO";
//                    String autoInc = rs.getString("IS_AUTOINCREMENT");
//                    System.out.println("Name - " + columnName + " Datatype - " + dataType + " Size - " + size + " Nullable - " + nullable + " Auto Increment - " + autoInc);
                }
            }
        }
        return columns;
    }

    //To get primary keys of each table

    public List<String> getPrimaryKeys(String tableName, CrawlerConfig crawlerConfig) throws Exception {

        String url = crawlerConfig.getDatabase().getJdbcUrl();
        String username = crawlerConfig.getDatabase().getUsername();
        String password = crawlerConfig.getDatabase().getPassword();

        List<String> primaryKeys = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(url, username, password)) {
            DatabaseMetaData metaData = conn.getMetaData();

            try(ResultSet rs = metaData.getPrimaryKeys(conn.getCatalog(), null, tableName)) {

//                getResultSetMetaData(rs);
//                System.out.println("\nPrimary Keys :\n");

                while(rs.next()) {
                    primaryKeys.add(rs.getString("COLUMN_NAME"));
//                    String pkColumn = rs.getString("COLUMN_NAME");
//                    System.out.println("- " + pkColumn);
                }
            }
        }
        return primaryKeys;
     }

     //To get foreign keys of each table

     public List<ForeignKeyModel> getForeignKeys(String tableName, CrawlerConfig crawlerConfig) throws Exception {

        String url = crawlerConfig.getDatabase().getJdbcUrl();
        String username = crawlerConfig.getDatabase().getUsername();
        String password = crawlerConfig.getDatabase().getPassword();

        List<ForeignKeyModel> foreignKeys = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(url, username, password)) {
            DatabaseMetaData metaData = conn.getMetaData();

            try(ResultSet rs = metaData.getImportedKeys(conn.getCatalog(), null, tableName)) {

//                getResultSetMetaData(rs);
//                System.out.println("\nForeign Keys :\n");

                while(rs.next()) {
                    ForeignKeyModel foreignKey = new ForeignKeyModel();

                    foreignKey.setColumnName(rs.getString("FKCOLUMN_NAME"));
                    foreignKey.setReferencedTable(rs.getString("PKTABLE_NAME"));
                    foreignKey.setReferencedColumn(rs.getString("PKCOLUMN_NAME"));

                    foreignKeys.add(foreignKey);
//                    String fkColumn = rs.getString("FKCOLUMN_NAME");
//                    String pkTable = rs.getString("PKTABLE_NAME");
//                    String pkColumn = rs.getString("PKCOLUMN_NAME");

//                    System.out.println("FK Column " + fkColumn + " -> PK Column " + pkColumn + "(" + pkTable + ")");
                }
            }
        }
        return foreignKeys;
     }

     public List<IndexModel> getIndexes(String tableName, CrawlerConfig crawlerConfig) throws SQLException {

        String url = crawlerConfig.getDatabase().getJdbcUrl();
        String username = crawlerConfig.getDatabase().getUsername();
        String password = crawlerConfig.getDatabase().getPassword();

        List<IndexModel> indexModels = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(url, username, password)) {
            DatabaseMetaData metadata = conn.getMetaData();
            try(ResultSet rs = metadata.getIndexInfo(conn.getCatalog(), null, tableName, false, false)) {
//                getResultSetMetaData(rs);
                while(rs.next()) {
                    IndexModel indexModel = new IndexModel();

                    indexModel.setName(rs.getString("INDEX_NAME"));
                    indexModel.setColumnName(rs.getString("COLUMN_NAME"));
                    indexModel.setNonUnique(rs.getBoolean("NON_UNIQUE"));

                    indexModels.add(indexModel);
                }
            }
        }
        return indexModels;
     }

//     @PostConstruct
//     public void getIndexCall() throws Exception{
//        List<String> tables = listTables(new CrawlerConfig());
//        for(String table : tables) {
//            getIndexes(table.split(" ")[0]);
//        }
//     }

//    public void printTableMetadata(String tableName) throws Exception {
//        System.out.println("TABLE: " + tableName + "/n");
//
//        getColumns(tableName);
//        getPrimaryKeys(tableName);
//        getForeignKeys(tableName);
//
//        System.out.println("----------------------------------------");
//    }

    //To create TableModel object for each table

    public TableModel buildTableModel(String tableName, CrawlerConfig crawlerConfig) throws Exception {
        TableModel table = new TableModel();

        table.setTableName(tableName);
        table.setColumns(getColumns(tableName, crawlerConfig));
        table.setPrimaryKeys(getPrimaryKeys(tableName, crawlerConfig));
        table.setForeignKeys(getForeignKeys(tableName, crawlerConfig));
        table.setIndexModels(getIndexes(tableName, crawlerConfig));

        return table;

//        try(Connection conn = dataSource.getConnection()) {
//            DatabaseMetaData metaData = conn.getMetaData();
//            try(ResultSet rs = metaData.getColumns(conn.getCatalog(), null, tableName, "%")) {
//                getResultSetMetaData(rs);
//                while(rs.next()) {
//                    ColumnModel col = new ColumnModel();
//                    col.setName(rs.getString("COLUMN_NAME"));
//                    col.setType(rs.getString("TYPE_NAME"));
//                    col.setSize(rs.getInt("COLUMN_SIZE"));
//                    col.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
//                    col.setAutoIncrement("YES".equals(rs.getString("IS_AUTOINCREMENT")));
//                    table.getColumns().add(col);
//                }
//            }
//            try(ResultSet rs = metaData.getPrimaryKeys(conn.getCatalog(), null, tableName)) {
//                getResultSetMetaData(rs);
//                while(rs.next()) {
//                    table.getPrimaryKeys().add(rs.getString("COLUMN_NAME"));
//                }
//            }
//            try(ResultSet rs = metaData.getImportedKeys(conn.getCatalog(), null, tableName)) {
//                getResultSetMetaData(rs);
//                while(rs.next()) {
//                    ForeignKeyModel fk = new ForeignKeyModel();
//                    fk.setColumnName(rs.getString("FKCOLUMN_NAME"));
//                    fk.setReferencedTable(rs.getString("PKTABLE_NAME"));
//                    fk.setReferencedColumn(rs.getString("PKCOLUMN_NAME"));
//                    table.getForeignKeys().add(fk);
//                }
//            }
//        }
    }


    public List<TableModel> getTableSchema(CrawlerConfig crawlerConfig){
        List<TableModel> models = new ArrayList<>();

        try {
            List<String> tables = listTables(crawlerConfig);

            for(String table : tables) {
//                System.out.println(table);
                TableModel model = buildTableModel(table.split(" ")[0], crawlerConfig);
                models.add(model);
//                System.out.println(model);
            }
        }
        catch(Exception e) {
            System.err.println("Failed to list tables: " + e.getMessage());
            e.printStackTrace();
        }

        return models;
    }


//    private void getResultSetMetaData(ResultSet rs) {
//        try {
//            ResultSetMetaData rsmd = rs.getMetaData();
//            int cols = rsmd.getColumnCount();
//
//            for(int i = 1; i <= cols; i++) {
//                System.out.println(rsmd.getColumnLabel(i));
//            }
//        }
//        catch(SQLException e) {
//            e.printStackTrace();
//        }
//
//    }
}
