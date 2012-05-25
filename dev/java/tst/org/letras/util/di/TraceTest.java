/*
 * Build by TU Darmstadt 2011, all rights reserved.
 */
package org.letras.util.di;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for traces in the digital ink data structure.
 * 
 * @author Felix Heinrichs <felix.heinrichs@cs.tu-darmstadt.de>
 * @version 0.3.0
 */
public class TraceTest {
	
	public TraceTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	/**
	 * Test of getSamples method, of class Trace.
	 */
	@Test
	public void testGetSamples() {
		Trace instance = new Trace("testPen");
		assertNotNull(instance.getSamples());
	}

	/**
	 * Test of setSamples method, of class Trace.
	 */
	@Test
	public void testSetSamples() {
	}

	/**
	 * Test of timeFrame method, of class Trace.
	 */
	@Test
	public void testTimeFrame() {
	}

	/**
	 * Test of boundingBox method, of class Trace.
	 */
	@Test
	public void testBoundingBox() {
	}

	/**
	 * Test of pathLength method, of class Trace.
	 */
	@Test
	public void testPathLength() {
	}

	/**
	 * Test of computeBoundingBox method, of class Trace.
	 */
	@Test
	public void testComputeBoundingBox() {
	}

	/**
	 * Test of computeTimeFrame method, of class Trace.
	 */
	@Test
	public void testComputeTimeFrame() {
	}

	/**
	 * Test of computePathLength method, of class Trace.
	 */
	@Test
	public void testComputePathLength() {
	}

	/**
	 * Test of add method, of class Trace.
	 */
	@Test
	public void testAdd() {
	}
}
