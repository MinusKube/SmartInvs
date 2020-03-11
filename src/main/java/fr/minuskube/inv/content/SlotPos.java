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

package fr.minuskube.inv.content;

/**
 * Represents the position (row + column) of a slot
 * in an inventory.
 */
public final class SlotPos {

    private final int row;

    private final int column;

    public SlotPos(final int row, final int column) {
        this.row = row;
        this.column = column;
    }

    public static SlotPos of(final int row, final int column) {
        return new SlotPos(row, column);
    }

    @Override
    public int hashCode() {
        int result = this.row;
        result = 31 * result + this.column;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final SlotPos slotPos = (SlotPos) obj;
        return this.row == slotPos.row && this.column == slotPos.column;
    }

    @Override
    public String toString() {
        return "SlotPos{" +
            "row=" + this.row +
            ", column=" + this.column +
            '}';
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

}
