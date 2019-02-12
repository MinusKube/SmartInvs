package fr.minuskube.inv.util;

import com.google.common.base.Preconditions;
import fr.minuskube.inv.content.SlotPos;

import java.util.HashMap;
import java.util.Map;

public class Pattern<T> {

    private T defaultValue;

    private final String[] lines;
    private Map<Character, T> mapping = new HashMap<>();

    public Pattern(String... lines) {
        Preconditions.checkArgument(lines.length > 0, "The given pattern lines must not be empty.");

        int columnCount = lines[0].length();

        for(int i = 0; i < lines.length; i++) {
            String line = lines[i];

            Preconditions.checkArgument(line.length() == columnCount,
                    "The given pattern line %s does not match the first line character count.", i);
        }

        this.lines = lines;
    }

    public void attach(char character, T object) {
        this.mapping.put(character, object);
    }

    public T getObject(int index) {
        int columnCount = this.getColumnCount();

        return this.getObject(index / columnCount, index % columnCount);
    }

    public T getObject(SlotPos slot) {
        return this.getObject(slot.getRow(), slot.getColumn());
    }

    public T getObject(int row, int column) {
        Preconditions.checkArgument(row >= 0 && row < this.lines.length,
                "The row must be between 0 and the row count");
        Preconditions.checkArgument(column >= 0 && column < this.lines[row].length(),
                "The column must be between 0 and the column count");

        return this.mapping.getOrDefault(this.lines[row].charAt(column), this.defaultValue);
    }

    public T getDefault() { return this.defaultValue; }
    public void setDefault(T defaultValue) { this.defaultValue = defaultValue; }

    public int getRowCount() { return this.lines.length; }
    public int getColumnCount() { return this.lines[0].length(); }

}
