package fr.minuskube.inv.util;

import com.google.common.base.Preconditions;
import fr.minuskube.inv.content.SlotPos;

import java.util.*;

public class Pattern<T> {

    private T defaultValue;

    private final String[] lines;
    private Map<Character, T> mapping = new HashMap<>();

    private final boolean wrapAround;

    public Pattern(String... lines) {
        this(false, lines);
    }

    public Pattern(boolean wrapAround, String... lines) {
        Preconditions.checkNotNull(lines, "The given pattern lines must not be null.");
        Preconditions.checkArgument(lines.length > 0, "The given pattern lines must not be empty.");

        int columnCount = lines[0].length();

        this.lines = new String[lines.length];

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            Preconditions.checkNotNull(line, "The given pattern line %s cannot be null.", i);
            Preconditions.checkArgument(line.length() == columnCount,
                "The given pattern line %s does not match the first line character count.", i);
            this.lines[i] = lines[i];
        }

        this.wrapAround = wrapAround;
    }

    public Pattern<T> attach(char character, T object) {
        this.mapping.put(character, object);
        return this;
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
            Preconditions.checkElementIndex(row, this.lines.length, "The row must be between 0 and the row count");
            Preconditions.checkElementIndex(column, this.lines[0].length(), "The column must be between 0 and the column size");
        }
        return this.mapping.getOrDefault(this.lines[row].charAt(column), this.defaultValue);
    }

    public Optional<SlotPos> findKey(char character) {
        for (int row = 0; row < getRowCount(); row++) {
            for (int column = 0; column < getColumnCount(); column++) {
                if (this.lines[row].charAt(column) == character) {
                    return Optional.of(SlotPos.of(row, column));
                }
            }
        }
        return Optional.empty();
    }

    public List<SlotPos> findAllKeys(char character) {
        List<SlotPos> positions = new ArrayList<>();
        for (int row = 0; row < getRowCount(); row++) {
            for (int column = 0; column < getColumnCount(); column++) {
                if (this.lines[row].charAt(column) == character) {
                    positions.add(SlotPos.of(row, column));
                }
            }
        }
        return positions;
    }

    public T getDefault() {
        return this.defaultValue;
    }

    public Pattern<T> setDefault(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
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
