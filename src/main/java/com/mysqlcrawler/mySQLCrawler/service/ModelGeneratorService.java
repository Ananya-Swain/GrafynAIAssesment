package com.mysqlcrawler.mySQLCrawler.service;

import com.mysqlcrawler.mySQLCrawler.model.ColumnModel;
import com.mysqlcrawler.mySQLCrawler.model.ForeignKeyModel;
import com.mysqlcrawler.mySQLCrawler.model.TableModel;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class ModelGeneratorService {
    private final String basePackage = "com.mysqlcrawler.mySQLCrawler.generated";

    private String toCamelCase(String name, boolean startWithUpper) {

        StringBuilder sb = new StringBuilder();
        char[] arr = name.toCharArray();

        for(char ch : arr) {
             if(ch == '_' || ch == ' ') {
                 startWithUpper = true;
             }
             else if(ch >= 97 && ch <= 122 && startWithUpper) {
                 sb.append(Character.toUpperCase(ch));
                 startWithUpper = false;
             }
             else {
                 sb.append(ch);
             }
        }

        return sb.toString();
    }

    private String mapSqlType(String sqlType) {
        sqlType = sqlType.toLowerCase();

        if(sqlType.contains("int"))
            return "Integer";
        if(sqlType.contains("bigint"))
            return "Long";
        if(sqlType.contains("timestamp"))
            return "java.sql.Timestamp";

        return "String";
    }

    private void saveToFile(String className, String body) throws IOException {
        String path = "src/main/java/" + basePackage.replace(".", "/") + "/" + className + ".java";

        System.out.println("basePackage : " + basePackage);
        System.out.println("path : " + path);
        FileWriter writer = new FileWriter(path);
        writer.write(body);
        writer.close();
    }

    private String generateIdClass(TableModel table) throws IOException{
        StringBuilder sb = new StringBuilder();
        String className = toCamelCase(table.getTableName(), true) + "Id";

        sb.append("package ").append(basePackage).append(";\n\n");
        sb.append("import jakarta.persistence.*;\n");
        sb.append("import lombok.Data;\n\n");
        sb.append("import java.io.Serializable;\n\n");

        sb.append("@Embeddable\n");
        sb.append("@Data\n");
        sb.append("public class ").append(className).append(" implements Serializable").append(" {\n\n");

        for(ColumnModel col : table.getColumns()) {
            String fieldName = toCamelCase(col.getName(), false);
            String fieldType = mapSqlType(col.getType());

            sb.append(" private ").append(fieldType).append(" ").append(fieldName).append(";\n\n");
        }

        sb.append("}");

        saveToFile(className, sb.toString());

        return className;
    }

    private void generateModel(TableModel table) throws IOException {

        String className = toCamelCase(table.getTableName(), true);
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(basePackage).append(";\n\n");

        sb.append("import jakarta.persistence.*;\n");
        sb.append("import lombok.Data;\n\n");

        sb.append("@Data\n");
        sb.append("@Entity\n");
        sb.append("@Table(name = \"").append(table.getTableName()).append("\")\n");
        sb.append("public class ").append(className).append(" {\n\n");

        boolean hasManyToMany = false;

        if(table.getColumns().size() == table.getPrimaryKeys().size() && table.getPrimaryKeys().size() == table.getForeignKeys().size()) {
            String idClassName = generateIdClass(table);
            sb.append(" @EmbeddedId\n");
            sb.append(" private " + idClassName + " id;\n\n");
            hasManyToMany = true;
        }
        System.out.println(className);

        for(ColumnModel col : table.getColumns()) {
            String fieldName = toCamelCase(col.getName(), false);
            String fieldType = mapSqlType(col.getType());
            boolean isFK = false;
            System.out.println(fieldName);
            for(ForeignKeyModel fk : table.getForeignKeys()) {
                String fkFieldName = toCamelCase(fk.getColumnName(), false);
                System.out.println("FKName : " + fkFieldName);
                if(fieldName.equals(fkFieldName)) {
                    isFK = true;
                    System.out.println("***");
                    break;
                }
                System.out.println("&&&");
            }

            if(isFK) {
                System.out.println("%%%");
                continue;
            }

            if(table.getPrimaryKeys().contains(col.getName())) {
                System.out.println("^^^");
                sb.append(" @Id\n");
                if(col.isAutoIncrement()) {
                    sb.append(" @GeneratedValue(strategy = GenerationType.IDENTITY)\n");
                }
            }

            sb.append(" @Column(name = \"").append(col.getName()).append("\")\n");
            sb.append(" private ").append(fieldType).append(" ").append(fieldName).append(";\n\n");
        }

        for(ForeignKeyModel fk : table.getForeignKeys()) {
            String fieldName = toCamelCase(fk.getReferencedTable(), false);
            String classType = toCamelCase(fk.getReferencedTable(), true);

            if(hasManyToMany) {
                sb.append(" @MapsId(\"").append(toCamelCase(fk.getColumnName(), false)).append("\")");
            }
            sb.append(" @ManyToOne\n");
            sb.append(" @JoinColumn(name = \"").append(fk.getColumnName()).append("\")\n");
            sb.append(" private ").append(classType).append(" ").append(fieldName).append(";\n\n");
        }

        sb.append("}\n");

        saveToFile(className, sb.toString());
    }

    public void generatedModels(List<TableModel> tables) throws IOException {
        for(TableModel table : tables) {
            generateModel(table);
        }
    }


}
