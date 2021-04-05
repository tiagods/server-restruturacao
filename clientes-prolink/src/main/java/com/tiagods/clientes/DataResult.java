package com.tiagods.clientes;

import java.util.List;

public class DataResult {
    private final List<String> columnNames ;
    private final List<List<Object>> data ;

    public DataResult(List<String> columnNames, List<List<Object>> data) {
        this.columnNames = columnNames ;
        this.data = data ;
    }

    public int getNumColumns() {
        return columnNames.size();
    }

    public String getColumnName(int index) {
        return columnNames.get(index);
    }

    public int getColumnIndex(String name) {
        int index = -1;
        for(int i = 0; i< columnNames.size(); i++) {
            String nameColumn = columnNames.get(i);
            if(name.equals(nameColumn)) {
                index = i;
            }
        }
        return index;
    }

    public int getNumRows() {
        return data.size();
    }

    public Object getData(int column, int row) {
        return data.get(row).get(column);
    }

    public List<List<Object>> getData() {
        return data ;
    }

    public List<String> getColumns() {
        return columnNames;
    }
}
