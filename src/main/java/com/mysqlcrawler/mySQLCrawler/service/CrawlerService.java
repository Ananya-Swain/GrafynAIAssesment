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

    public List<String> listTables(UserConfig userConfig) throws Exception{
        List<String> tables = new ArrayList<>();

        String url = userConfig.getDatabase().getJdbcUrl();
        String username = userConfig.getDatabase().getUsername();
        String password = userConfig.getDatabase().getPassword();

        try(Connection conn = DriverManager.getConnection(url, username, password)) {
            DatabaseMetaData metaData = conn.getMetaData();
//            System.out.println("catalog : " + conn.getCatalog());

            boolean includeViews = userConfig.getCrawler().isIncludeViews();

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

    public List<ColumnModel> getColumns(String tableName, UserConfig userConfig) throws Exception {

        String url = userConfig.getDatabase().getJdbcUrl();
        String username = userConfig.getDatabase().getUsername();
        String password = userConfig.getDatabase().getPassword();

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

    public List<String> getPrimaryKeys(String tableName, UserConfig userConfig) throws Exception {

        String url = userConfig.getDatabase().getJdbcUrl();
        String username = userConfig.getDatabase().getUsername();
        String password = userConfig.getDatabase().getPassword();

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

     public List<ForeignKeyModel> getForeignKeys(String tableName, UserConfig userConfig) throws Exception {

        String url = userConfig.getDatabase().getJdbcUrl();
        String username = userConfig.getDatabase().getUsername();
        String password = userConfig.getDatabase().getPassword();

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

     //To get indexes of each table

     public List<IndexModel> getIndexes(String tableName, UserConfig userConfig) throws SQLException {

        String url = userConfig.getDatabase().getJdbcUrl();
        String username = userConfig.getDatabase().getUsername();
        String password = userConfig.getDatabase().getPassword();

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

    //To create TableModel object for each table

    public TableModel buildTableModel(String tableName, UserConfig userConfig) throws Exception {
        TableModel table = new TableModel();

        table.setTableName(tableName);
        table.setColumns(getColumns(tableName, userConfig));
        table.setPrimaryKeys(getPrimaryKeys(tableName, userConfig));
        table.setForeignKeys(getForeignKeys(tableName, userConfig));
        table.setIndexes(getIndexes(tableName, userConfig));

        return table;
    }


    public List<TableModel> getTableSchema(UserConfig userConfig){
        List<TableModel> models = new ArrayList<>();

        try {
            List<String> tables = listTables(userConfig);

            for(String table : tables) {
                TableModel model = buildTableModel(table.split(" ")[0], userConfig);
                models.add(model);
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
