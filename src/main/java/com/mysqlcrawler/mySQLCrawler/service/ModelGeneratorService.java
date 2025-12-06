package com.mysqlcrawler.mySQLCrawler.service;

import com.mysqlcrawler.mySQLCrawler.model.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Service
public class ModelGeneratorService {
    private String basePackage = "com.mysqlcrawler.mySQLCrawler.generated";

    Map<TableModel, List<String>> manyToManyMapping = new HashMap<>();
    Map<String, List<String>> oneToManyMapping = new HashMap<>();

    List<GeneratedTableInfo> generatedModels = new ArrayList<>();

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

//        System.out.println("basePackage : " + basePackage);
//        System.out.println("path : " + path);
        FileWriter writer = new FileWriter(path);
        writer.write(body);
        writer.close();
    }

    private String generateIdClass(TableModel table) throws IOException{
//        System.out.println("generatedIdClass is called.");

        GeneratedTableInfo generatedTable = new GeneratedTableInfo();

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

            sb.append("\tprivate ").append(fieldType).append(" ").append(fieldName).append(";\n\n");
        }

        sb.append("}");

        generatedTable.setClassName(className);
        generatedTable.setBody(sb.toString());
        generatedModels.add(generatedTable);

        saveToFile(className, sb.toString());

        return className;
    }

    private void generateModel(TableModel table, boolean generateManyToMany) throws IOException {

        GeneratedTableInfo generatedTable = new GeneratedTableInfo();

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

        if(manyToManyMapping.containsKey(table)) {
            String idClassName = generateIdClass(table);
            sb.append("\t@EmbeddedId\n");
            sb.append("\tprivate " + idClassName + " id;\n\n");
            hasManyToMany = true;
        }

//        if(table.getColumns().size() == table.getPrimaryKeys().size() && table.getPrimaryKeys().size() == table.getForeignKeys().size()) {
//            String idClassName = generateIdClass(table);
//            sb.append(" @EmbeddedId\n");
//            sb.append(" private " + idClassName + " id;\n\n");
//            hasManyToMany = true;
//        }
//        System.out.println(className);

        for(ColumnModel col : table.getColumns()) {
            String fieldName = toCamelCase(col.getName(), false);
            String fieldType = mapSqlType(col.getType());
            boolean isFK = false;
//            System.out.println(fieldName);
            for(ForeignKeyModel fk : table.getForeignKeys()) {
                String fkFieldName = toCamelCase(fk.getColumnName(), false);
//                System.out.println("FKName : " + fkFieldName);
                if(fieldName.equals(fkFieldName)) {
                    isFK = true;
//                    System.out.println("***");
                    break;
                }
//                System.out.println("&&&");
            }

            if(isFK) {
//                System.out.println("%%%");
                continue;
            }

            if(table.getPrimaryKeys().contains(col.getName())) {
//                System.out.println("^^^");
                sb.append("\t@Id\n");
                if(col.isAutoIncrement()) {
                    sb.append("\t@GeneratedValue(strategy = GenerationType.IDENTITY)\n");
                }
            }

            sb.append("\t@Column(name = \"").append(col.getName()).append("\")\n");
            sb.append("\tprivate ").append(fieldType).append(" ").append(fieldName).append(";\n\n");
        }

        for(ForeignKeyModel fk : table.getForeignKeys()) {
            String fieldName = toCamelCase(fk.getReferencedTable(), false);
            String classType = toCamelCase(fk.getReferencedTable(), true);

            if(hasManyToMany) {
                sb.append("\t@MapsId(\"").append(toCamelCase(fk.getColumnName(), false)).append("\")");
            }
            sb.append("\t@ManyToOne\n");
            sb.append("\t@JoinColumn(name = \"").append(fk.getColumnName()).append("\")\n");
            sb.append("\tprivate ").append(classType).append(" ").append(fieldName).append(";\n\n");
        }

        if(generateManyToMany) {

            for (Map.Entry<TableModel, List<String>> entry : manyToManyMapping.entrySet()) {
                if (entry.getValue().get(0).equals(table.getTableName())) {
                    sb.append("\t@ManyToMany\n");
                    sb.append("\t@JoinTable(\n");
                    sb.append("\t\tname = \"").append(entry.getKey().getTableName()).append("\",\n");
                    String joinColumn;
                    String joinTable;
                    String inverseJoinColumn;
                    String inverseJoinTable;
                    if (entry.getKey().getForeignKeys().get(0).getReferencedTable().equals(table.getTableName())) {
                        joinColumn = entry.getKey().getForeignKeys().get(0).getColumnName();
                        joinTable = entry.getKey().getForeignKeys().get(0).getReferencedTable();
                        inverseJoinColumn = entry.getKey().getForeignKeys().get(1).getColumnName();
                        inverseJoinTable = entry.getKey().getForeignKeys().get(1).getReferencedTable();
                    } else {
                        joinColumn = entry.getKey().getForeignKeys().get(1).getColumnName();
                        joinTable = entry.getKey().getForeignKeys().get(1).getReferencedTable();
                        inverseJoinColumn = entry.getKey().getForeignKeys().get(0).getColumnName();
                        inverseJoinTable = entry.getKey().getForeignKeys().get(0).getReferencedTable();
                    }

                    int index = sb.indexOf("import");
                    sb.insert(index, "import java.util.Set;\n");
                    sb.append("\t\tjoinColumns = @JoinColumn(name = \"").append(joinColumn).append("\"),\n");
                    sb.append("\t\tinverseJoinColumns = @JoinColumn(name = \"").append(inverseJoinColumn).append("\")\n");
                    sb.append("\t)\n");

                    sb.append("\tprivate Set<").append(toCamelCase(inverseJoinTable, true)).append("> ").append(inverseJoinTable).append(";\n");
                }

                if (entry.getValue().get(1).equals(table.getTableName())) {
                    String joinColumn;
                    String joinTable;
                    String inverseJoinColumn;
                    String inverseJoinTable;
                    if (entry.getKey().getForeignKeys().get(0).getReferencedTable().equals(table.getTableName())) {
                        joinColumn = entry.getKey().getForeignKeys().get(0).getColumnName();
                        joinTable = entry.getKey().getForeignKeys().get(0).getReferencedTable();
                        inverseJoinColumn = entry.getKey().getForeignKeys().get(1).getColumnName();
                        inverseJoinTable = entry.getKey().getForeignKeys().get(1).getReferencedTable();
                    } else {
                        joinColumn = entry.getKey().getForeignKeys().get(1).getColumnName();
                        joinTable = entry.getKey().getForeignKeys().get(1).getReferencedTable();
                        inverseJoinColumn = entry.getKey().getForeignKeys().get(0).getColumnName();
                        inverseJoinTable = entry.getKey().getForeignKeys().get(0).getReferencedTable();
                    }
                    int index = sb.indexOf("import");
                    sb.insert(index, "import java.util.Set;\n");
                    sb.append("\n\t@ManyToMany(mappedBy = \"").append(joinTable).append("\")\n");
                    sb.append("\tprivate Set<").append(toCamelCase(inverseJoinTable, true)).append("> ").append(inverseJoinTable).append(";\n");
                }
            }
        }

        if(oneToManyMapping.containsKey(table.getTableName())) {
            List<String> referencingTables = oneToManyMapping.get(table.getTableName());
            int index = sb.indexOf("import");
            sb.insert(index, "import java.util.List;\n");
            for(String referencingTable : referencingTables) {
                sb.append("\n\t@OneToMany(mappedBy = \"").append(table.getTableName()).append("\")\n");
                sb.append("\tprivate List<").append(toCamelCase(referencingTable, true)).append("> ").append(referencingTable).append(";\n");
            }
        }

        generatedTable.setClassName(className);
        generatedTable.setBody(sb.toString());
        generatedModels.add(generatedTable);

        sb.append("}\n");

        saveToFile(className, sb.toString());
    }

    public List<GeneratedTableInfo> generatedModels(List<TableModel> tables, UserConfig userConfig) throws IOException {

        String directory = userConfig.getCrawler().getOutputFolder();
        String[] paths = directory.split("/");

//        System.out.println("basePackage : " + basePackage);
        String[] basePaths = basePackage.split("\\.");
//        for(String path : basePaths) {
//            System.out.println(path);
//        }
//        System.out.println(basePaths.length - 1);
        basePaths[basePaths.length - 1] = paths[paths.length - 1];

        basePackage = String.join(".", basePaths);
//        "src/main/java/" + basePackage.replace(".", "/")

        basePaths[basePaths.length - 1] = "";
        String parentDirectory = "src/main/java/" + String.join("/", basePaths);
        parentDirectory = parentDirectory.substring(0, parentDirectory.length() - 1);
        String newDirectory = paths[paths.length - 1];
//        System.out.println("basePackage : " + basePackage);
//        System.out.println("paretDirectory : " + parentDirectory);
//        System.out.println("childDirectory : " + newDirectory);

        File parentDir = new File(parentDirectory);
        File newDir = new File(parentDir, newDirectory);

        if(newDir.mkdirs()) {
//            System.out.println(newDirectory + "Directory created successfully.");
        }

        boolean generateManyToMany = userConfig.getCrawler().isDetectManyToMany();
        boolean generateOneToMany = userConfig.getCrawler().isGenerateBidirectional();
        generateRelationships(tables, generateManyToMany, generateOneToMany);
        System.out.println("ManyToMany : " + manyToManyMapping);
        System.out.println("OneToMany : " + oneToManyMapping);
        for(TableModel table : tables) {
            generateModel(table, generateManyToMany);
        }
        return generatedModels;
    }

    public void generateRelationships(List<TableModel> tables, boolean generateManyToMany, boolean generateOneToMany) {
        for(TableModel table : tables) {
            if(table.getColumns().size() == 2 && table.getPrimaryKeys().size() == 2 && table.getForeignKeys().size() == 2) {
                String table1 = table.getForeignKeys().get(0).getReferencedTable();
                String table2 = table.getForeignKeys().get(1).getReferencedTable();
                List<String> joinedTables = new ArrayList<>();
                joinedTables.add(table1);
                joinedTables.add(table2);

                manyToManyMapping.put(table, joinedTables);
            }
            else if(generateOneToMany && !table.getForeignKeys().isEmpty()) {
                System.out.println("Table Name : " + table.getTableName());
                for(ForeignKeyModel foreignKeyModel : table.getForeignKeys()) {
                    String referencedTable = foreignKeyModel.getReferencedTable();
                    if(oneToManyMapping.containsKey(referencedTable)) {
                        if(!oneToManyMapping.get(referencedTable).contains(table.getTableName()))
                            oneToManyMapping.get(referencedTable).add(table.getTableName());
                    }
                    else {
                        List<String> referencingTable = new ArrayList<>();
                        referencingTable.add(table.getTableName());
                        oneToManyMapping.put(referencedTable, referencingTable);
                    }
                }
            }
        }
    }
}
