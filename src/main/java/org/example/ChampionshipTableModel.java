package org.example;

import javax.swing.table.AbstractTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChampionshipTableModel extends AbstractTableModel {
    private List<Object[]> data;
    private String[] columnNames;

    public ChampionshipTableModel() {
        data = new ArrayList<>();
        columnNames = new String[]{"ID", "Name", "Date", "Address", "Prizes"};
    }

    public void setResultSet(ResultSet resultSet) throws SQLException {
        data.clear();

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            Object[] row = new Object[columnCount];

            for (int i = 0; i < columnCount; i++) {
                row[i] = resultSet.getObject(i + 1);
            }

            data.add(row);
        }

        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object[] row = data.get(rowIndex);
        return row[columnIndex];
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}