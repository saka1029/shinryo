package saka1029.shinryo.parser;

import static org.junit.Assert.assertEquals;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.junit.Test;

import saka1029.shinryo.common.Common;

public class TestToken {

    static Logger logger = Common.logger(TestToken.class);

    @Test
    public void testHeader1() {
        assertEquals("aaa", "aaa".replaceFirst("\\s.*", ""));
        assertEquals("aaa", "aaa bbb".replaceFirst("\\s.*", ""));
        assertEquals("aaa", "aaa bbb ccc".replaceFirst("\\s.*", ""));
    }
    
    @Test
    public void testHeader2() {
        assertEquals("", "aaa".replaceFirst("\\S*\\s*", ""));
        assertEquals("bbb", "aaa bbb".replaceFirst("\\S*\\s*", ""));
        assertEquals("bbb ccc", "aaa bbb ccc".replaceFirst("\\S*\\s*", ""));
    }
    
    @Test
    public void testDeque() {
        Deque<String> stack = new LinkedList<>();
        stack.push("a");
        stack.push("b");
        stack.push("c");
        Iterator<String> it = stack.iterator();
        assertEquals("c", it.next());
        assertEquals("b", it.next());
        assertEquals("a", it.next());
        Iterator<String> rit = stack.descendingIterator();
        assertEquals("a", rit.next());
        assertEquals("b", rit.next());
        assertEquals("c", rit.next());
    }

}
