package com.mysqlcrawler.mySQLCrawler.service;

import com.mysqlcrawler.mySQLCrawler.model.ForeignKeyModel;
import com.mysqlcrawler.mySQLCrawler.model.TableModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GetRelationshipService {
    public void generateRelationships(List<TableModel> tables) {
        Map<TableModel, List<String>> manyToManyMapping = new HashMap<>();
        Map<String, List<String>> oneToManyMapping = new HashMap<>();
        for(TableModel table : tables) {
            if(table.getColumns().size() == 2 && table.getPrimaryKeys().size() == 2 && table.getForeignKeys().size() == 2) {
                String table1 = table.getForeignKeys().get(0).getReferencedTable();
                String table2 = table.getForeignKeys().get(1).getReferencedTable();
                List<String> joinedTables = new ArrayList<>();
                joinedTables.add(table1);
                joinedTables.add(table2);

                manyToManyMapping.put(table, joinedTables);
            }
            else if(table.getForeignKeys().size() > 0) {
                for(ForeignKeyModel foreignKeyModel : table.getForeignKeys()) {
                    String referencedTable = foreignKeyModel.getReferencedTable();
                    if(oneToManyMapping.containsKey(referencedTable)) {
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
