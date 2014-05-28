package org.azkfw.crawler;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for Crawler.
 * 
 * @since 1.0.0
 * @version 1.0.0 2014/05/28
 * @author Kawakicchi
 */
public class CrawlerTest extends TestCase {

	/**
	 * Create the test case
	 * 
	 * @param testName name of the test case
	 */
	public CrawlerTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(CrawlerTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		assertTrue(true);
	}
}
