package fr.minuskube.inv.util;

import fr.minuskube.inv.content.SlotPos;
import org.junit.Test;

import static org.junit.Assert.*;

public class PatternTest {

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPattern() {
        new Pattern<>();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnequalColumnsPattern() {
        new Pattern<>(
                "X   X",
                "X    X",
                "X   X"
        );
    }

    @Test
    public void testRowColumnCountPattern() {
        Pattern<String> pattern = new Pattern<>(
                "XOOOX",
                "XOXOX",
                "XOOOX"
        );

        assertEquals(3, pattern.getRowCount());
        assertEquals(5, pattern.getColumnCount());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testNegativeGetPattern() {
        Pattern<String> pattern = new Pattern<>(
                "XOOX",
                "XOOX"
        );

        pattern.getObject(-1, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testOversizeGetPattern() {
        Pattern<String> pattern = new Pattern<>(
                "XOOX",
                "XOOX"
        );

        pattern.getObject(0, 4);
    }

    @Test
    public void testAttachPattern() {
        Pattern<String> pattern = new Pattern<>(
                "XXXXXXX",
                "XOOOOOX",
                "XOOOOOX",
                "XOOOOOX",
                "XOOOOOX",
                "XXXXXXX"
        );

        pattern.setDefault("Empty");
        pattern.attach('X', "Full");

        for(int row = 0; row < pattern.getRowCount(); row++) {
            for(int column = 0; column < pattern.getColumnCount(); column++) {
                String expected;

                if(row == 0 || row == (pattern.getRowCount() - 1)
                        || column == 0 || column == (pattern.getColumnCount() - 1)) {

                    expected = "Full";
                }
                else {
                    expected = "Empty";
                }

                assertEquals(expected, pattern.getObject(row, column));
                assertEquals(expected, pattern.getObject(SlotPos.of(row, column)));
            }
        }
    }

}
