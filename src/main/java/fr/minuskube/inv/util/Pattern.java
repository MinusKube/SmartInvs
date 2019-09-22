package fr.minuskube.inv.util;

import fr.minuskube.inv.content.SlotPos;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.*;

public class Pattern<T> {

    private T defaultValue;

    private final String[] lines;
    private Map<Character, T> mapping = new HashMap<>();

    private final boolean wrapAround;

    public Pattern(String... lines) {
        this(false, lines);
    }

    public Pattern(boolean wrapAround, String... lines) {
        checkNotNull(lines, "The given pattern lines must not be null.");
        checkArgument(lines.length > 0, "The given pattern lines must not be empty.");

        int columnCount = lines[0].length();

        this.lines = new String[lines.length];

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            checkNotNull(line, "The given pattern line %s cannot be null.", i);
            checkArgument(line.length() == columnCount,
                    "The given pattern line %s does not match the first line character count.", i);
            this.lines[i] = lines[i];
        }

        this.wrapAround = wrapAround;
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
        if (wrapAround) { // Prevent overflow of numbers. Allows for infinite repeating patterns.
            row %= getRowCount();
            if (row < 0)
                row += getRowCount();
            column %= getColumnCount();
            if (column < 0)
                column += getColumnCount();
        } else {
            checkElementIndex(row, this.lines.length, "The row must be between 0 and the row count");
            checkElementIndex(column, this.lines[0].length(), "The column must be between 0 and the column size");
        }
        return this.mapping.getOrDefault(this.lines[row].charAt(column), this.defaultValue);
    }

    public T getDefault() {
        return this.defaultValue;
    }

    public void setDefault(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getRowCount() {
        return this.lines.length;
    }

    public int getColumnCount() {
        return this.lines[0].length();
    }

    public boolean isWrapAround() {
        return wrapAround;
    }
}
