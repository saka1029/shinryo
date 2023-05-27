package saka1029.shinryo.parser0;

import static org.junit.Assert.*;

import org.junit.Test;

import saka1029.shinryo.parser0.ItemPattern;
import saka1029.shinryo.parser0.NumberPattern;

public class TestItemPattern {

	static final ItemPattern 別表第漢数字の = ItemPattern.conj("別表第", NumberPattern.漢数字, "", "の");
	static final ItemPattern 区分番号 = ItemPattern.simple("", NumberPattern.区分番号, "");
	static final ItemPattern 数字の = ItemPattern.conj("", NumberPattern.数字, "", "の");

	@Test
	public void testId() {
		assertEquals("5-2-3", 別表第漢数字の.id("別表第五の二の三"));
		assertEquals("5-25-31", 別表第漢数字の.id("別表第五の二十五の三十一"));
		assertEquals("A100-3-2", 区分番号.id("Ａ１００－３－２"));
		assertEquals("A100-3-2", 区分番号.id("Ａ１００―３－２"));
		assertEquals("1-22-3", 数字の.id("１の２２の３"));
	}

	@Test
	public void testPattern() {
		assertTrue(別表第漢数字の.pattern.matcher("別表第五の二の三").matches());
		assertTrue(別表第漢数字の.pattern.matcher("別表第五の二十五の三十一").matches());
		assertTrue(区分番号.pattern.matcher("Ａ１００－３－２").matches());
		assertTrue(区分番号.pattern.matcher("Ａ１００―３－２").matches());
		assertTrue(数字の.pattern.matcher("１の２２の３").matches());
	}

}
