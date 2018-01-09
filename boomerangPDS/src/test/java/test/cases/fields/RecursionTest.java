package test.cases.fields;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import test.core.AbstractBoomerangTest;

public class RecursionTest extends AbstractBoomerangTest {

	public class N {
		public Object value;
		public N next;

		public N(Object value) {
			this.value = value;
			next = null;
		}
	}

	public N recursive(int i, N m) {
		if (i < 2) {
			int j = i + 1;
			return recursive(j, m.next);
		}
		return m;
	}

	@Test
	public void test() {
		N node1 = new N(new Object());
		N node2 = new N(new Object());
		N node3 = new N(new Alloc());
		node1.next = node2;
		node2.next = node3;
		N n = recursive(0, node1);

		Object code = n.value;
		queryFor(code);
	}

}
