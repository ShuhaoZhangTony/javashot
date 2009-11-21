/**
 * Copyright (C) 2009 Samir Chekkal
 * This program is distributed under the terms of the GNU General Public License.
 */
package test;

/**
 * @author chekkal
 * 
 */
public class Person {
	public void startActivity() {
		bookReading();
	}

	private void bookReading() {
		Book book = new Book();
		book.open();
		book.read();
		book.close();
	}
}
