package test.cases.string;

import org.junit.Test;

import test.cases.fields.Alloc;
import test.core.AbstractBoomerangTest;

public class StringTest extends AbstractBoomerangTest {
	@Test
	public void stringConcat(){
		Object query = "a" + "b";
		if(staticallyUnknown())
			query += "c";
		System.out.println(query);
		queryFor(query);
	}
	@Test
	public void stringToCharArray(){
		char[] s = "password".toCharArray();
		queryFor(s);
	}
	

	@Test
	public void stringBuilderTest(){
		StringBuilder b = new StringBuilder("Test");
		b.append("ABC");
		String s = b.toString();
		queryFor(s);
	}
	@Test
	public void stringBuilder1Test(){
		StringBuilder b = new StringBuilder("Test");
		String s = b.toString();
		queryFor(s);
	}
}
