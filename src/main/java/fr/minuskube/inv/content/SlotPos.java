package fr.minuskube.inv.content;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SlotPos {

    private final int row;
    private final int column;

    public SlotPos(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;

        SlotPos slotPos = (SlotPos) object;

        return new EqualsBuilder()
                .append(row, slotPos.row)
                .append(column, slotPos.column)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(row)
                .append(column)
                .toHashCode();
    }

    public int getRow() { return row; }
    public int getColumn() { return column; }

    public static SlotPos of(int row, int column) {
        return new SlotPos(row, column);
    }

}
