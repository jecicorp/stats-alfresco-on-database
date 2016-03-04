package fr.jeci.alfresco.saod;

import org.junit.Test;

public class StringUtilTest {

	@Test
	public void test() {
		System.out.println(StringUtil.readableFileSize(Integer.MAX_VALUE - 1));

		for (int i = 1; i <= 1024; i++) {
			System.out.print("2^" + i + " = ");
			System.out.println(StringUtil.readableFileSize(Math.pow(2, i) - 1));
		}
	}

}
