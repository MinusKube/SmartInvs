/*
 * Copyright 2018-2020 Isaac Montagne
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package fr.minuskube.inv.util;

import com.google.common.base.Preconditions;
import fr.minuskube.inv.content.SlotPos;

import java.util.*;

/**
 * A class representing a pattern with arbitrary keys and values
 *
 * @param <T> The type of the values that will be associated with the character keys
 */
public class Pattern<T> {

    private T defaultValue;

    private final String[] lines;
    private final Map<Character, T> mapping = new HashMap<>();

    private final boolean wrapAround;

    /**
     * Creates a new Pattern instance based on the provided lines
     * <br>
     * A instance created with this constructor is equal to {@link #Pattern(boolean, String...) Pattern(false, lines)}
     *
     * @param lines the lines describing the pattern
     *
     * @throws NullPointerException     If <code>lines</code> is null
     * @throws NullPointerException     If a string in lines is null
     * @throws IllegalArgumentException If the length of <code>lines</code> is zero
     * @throws IllegalArgumentException If the length of a line is not equal to the length of the first line
     * @see #Pattern(boolean, String...) To get the possibility to create a repeating pattern
     */
    public Pattern(String... lines) {
        this(false, lines);
    }

    /**
     * Creates a new Pattern instance based on the provided lines
     * <br>
     * When wrapAround is set to <code>true</code>, calls to {@link #getObject(SlotPos)} will not be out of bounds, but will start from the beginning again
     *
     * @param wrapAround whether the pattern should be repeated if the
     * @param lines      the lines describing the pattern
     *
     * @throws NullPointerException     If <code>lines</code> is null
     * @throws NullPointerException     If a string in lines is null
     * @throws IllegalArgumentException If the length of <code>lines</code> is zero
     * @throws IllegalArgumentException If the length of a line is not equal to the length of the first line
     */
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

    /**
     * Attaches an object to a character in this pattern instance.
     *
     * @param character The key character
     * @param object    The object to attach to that character
     *
     * @return <code>this</code> for a builder-like usage
     */
    public Pattern<T> attach(char character, T object) {
        this.mapping.put(character, object);
        return this;
    }

    /**
     * Returns the object from the n-th key in this pattern. If this pattern has wrapAround set to <code>true</code>, and the index is equals or greater than
     * the amount of individual positions in this pattern, it will continue downwards, and not wrap around sideways. Because of this, it could be unclear
     * what this method does and usage is for code clarity discouraged
     *
     * @param index The index in this pattern
     *
     * @return The object associated with the key
     *
     * @see #getObject(int, int) For more detailed information
     */
    public T getObject(int index) {
        int columnCount = this.getColumnCount();

        return this.getObject(index / columnCount, index % columnCount);
    }

    /**
     * This method is simple a shorthand to the method call {@link #getObject(int, int) getObject(slot.getRow(), slot.getColumn())},
     * so all the special cases described in that method will apply to this one
     *
     * @param slot The slot position to extract the row and column from
     *
     * @return The object associated with the key, or the default object
     *
     * @see #getObject(int, int) For the more detailed information
     */
    public T getObject(SlotPos slot) {
        return this.getObject(slot.getRow(), slot.getColumn());
    }

    /**
     * Retrieves the object associated with the key found at the row and column in this pattern, if there is no object attached to that character,
     * the default object set via {@link #setDefault(Object)} is used.
     * <br>
     * If wrapAround is set to <code>true</code> and the row or column would be too big or small of the pattern, it will wrap around and continue on from
     * the other side, like it would be endless. If not, {@link IndexOutOfBoundsException} will be thrown
     * <br>
     * <b>Warning:</b> This method can return <code>null</code>
     *
     * @param row    The row of the key
     * @param column The column of the key
     *
     * @return The object associated with the key, or the default object
     *
     * @throws IndexOutOfBoundsException If wrapAround is <code>false</code> and row or column are negative or not less that the patterns dimensions
     */
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

    /**
     * Searches through this patterns lines to find the first top-left occurrence of this key.
     * If it could not be found, the returned {@link Optional} is empty.
     *
     * @param character The character key to look for
     *
     * @return An optional containing the slot position in this pattern, or empty if it could not be found
     */
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

    /**
     * Searches through this patterns lines to find all occurrences of this key.
     * The first position is the most top-left and the last position is the most bottom-right one.
     * <br>
     * If the key isn't contained in this pattern, the returned list will be empty.
     *
     * @param character The character key to look for
     *
     * @return A mutable list containing all positions where that key occurs
     */
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

    /**
     * Returns the default value set via {@link #setDefault(Object)}
     * <br>
     * <b>Warning:</b> This method can return <code>null</code>, if a default value hasn't been set yet
     *
     * @return The default value
     */
    public T getDefault() {
        return this.defaultValue;
    }

    /**
     * Sets a new default value, which can be null and will override the previous value if present.
     *
     * @param defaultValue The new default value
     *
     * @return <code>this</code> for a builder-like usage
     */
    public Pattern<T> setDefault(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * This method counts the amount of rows this pattern has based on the amount of lines provided at creation
     *
     * @return the amount of rows
     */
    public int getRowCount() {
        return this.lines.length;
    }

    /**
     * This method counts the amount of rows this pattern has based on the length of the lines
     *
     * @return the amount of columns
     */
    public int getColumnCount() {
        return this.lines[0].length();
    }

    /**
     * A simple getter for the value provided at the Patterns creation, if this pattern supports wrapAround
     *
     * @return <code>true</code> if wrapAround is enabled for this instance
     */
    public boolean isWrapAround() {
        return wrapAround;
    }
}
