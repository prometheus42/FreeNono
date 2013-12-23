/*****************************************************************************
 * FreeNono - A free implementation of the nonogram game
 * Copyright (c) 2013 by FreeNono Development Team
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *****************************************************************************/
package org.freenono.model.game_modes;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests class for GameTime.
 * 
 * @author Christian Wichmann
 */
public class GameTimeTest {

	private static GameTime zero;
	private static GameTime minuteAndHalf;
	private static GameTime thirtyThreeMinutesAndEightSeconds;
	private static GameTime randomTime1;
	private static GameTime randomTime2;

	/**
	 * Sets up GameTime instances to test against.
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		Random rnd = new Random();
		zero = new GameTime(0);
		minuteAndHalf = new GameTime(90);
		thirtyThreeMinutesAndEightSeconds = new GameTime(33, 8);
		randomTime1 = new GameTime(rnd.nextInt(60), rnd.nextInt(60));
		randomTime2 = new GameTime(rnd.nextInt(5000));
	}

	/**
	 * Throw away test instances.
	 * 
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		zero = null;
		minuteAndHalf = null;
		thirtyThreeMinutesAndEightSeconds = null;
		randomTime1 = null;
		randomTime2 = null;
	}

	/**
	 * Test method for
	 * {@link org.freenono.model.game_modes.GameTime#GameTime(int, int)}.
	 */
	@Test
	public final void testGameTimeIntInt() {

		GameTime someTime = new GameTime(17, 42);
		assertEquals("", 17, someTime.getMinutes());
		assertEquals("", 42, someTime.getSeconds());
	}

	/**
	 * Tests for thrown exception when negative value is set. Test method for
	 * {@link org.freenono.model.game_modes.GameTime#GameTime(int)}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testGameTimeInt() {

		GameTime illegalTime = new GameTime(-42);
		illegalTime.getSeconds();
	}

	/**
	 * Tests conversion constructor for <code>long</code> arguments. Test method
	 * for {@link org.freenono.model.game_modes.GameTime#GameTime(long)}.
	 */
	@Test
	public final void testGameTimeLong() {

		long n = 2343;
		GameTime longTime = new GameTime(n);
		assertEquals("Minutes not correct.", 39, longTime.getMinutes());
		assertEquals("Seconds not correct.", 3, longTime.getSeconds());
	}

	/**
	 * Tests conversion constructor for <code>int</code> arguments with a small
	 * value as seconds. Test method for
	 * {@link org.freenono.model.game_modes.GameTime#GameTime(int)}.
	 */
	@Test
	public final void testGameTimeInt2() {

		final int seconds = 59;
		GameTime someTime = new GameTime(seconds);
		assertEquals("Minutes not correct.", 0, someTime.getMinutes());
		assertEquals("Seconds not correct.", seconds, someTime.getSeconds());
	}

	/**
	 * Tests for <code>String</code> representation of <code>GameTime</code>
	 * instance. Test method for
	 * {@link org.freenono.model.game_modes.GameTime#toString()} .
	 */
	@Test
	public final void testToString() {

		assertEquals("String representation not correct.", "00:00",
				zero.toString());
		assertEquals("String representation not correct.", "01:30",
				minuteAndHalf.toString());
		assertEquals("String representation not correct.", "33:08",
				thirtyThreeMinutesAndEightSeconds.toString());
	}

	/**
	 * Tests the equals method for <code>GameTime</code>. Test method for
	 * {@link org.freenono.model.game_modes.GameTime#equals()} .
	 */
	@Test
	public final void testEquals() {

		GameTime gm1 = new GameTime(1, 1, 1);
		GameTime gm2 = new GameTime(1, 1, 1);
		GameTime gm3 = new GameTime(3600 + 60 + 1);
		assertEquals("Single GameTime instance not equal to itself.", gm1, gm1);
		assertEquals(
				"Two GameTime instances with same constructors not equal.",
				gm1, gm2);
		assertEquals(
				"Two GameTime instances with different constructors not equal.",
				gm1, gm3);
		assertEquals("Two zeroed GameTime instances not equal.",
				new GameTime(), new GameTime(0));
	}

	/**
	 * Tests the hashCode method for <code>GameTime</code>. Test method for
	 * {@link org.freenono.model.game_modes.GameTime#hashCode()} .
	 */
	@Test
	public final void testHashCode() {

		GameTime gm1 = new GameTime(1, 1, 1);
		GameTime gm2 = new GameTime(1, 1, 1);
		GameTime gm3 = new GameTime(3600 + 60 + 1);
		assertEquals(
				"Single GameTime instance does not have same hash code as itself.",
				gm1.hashCode(), gm1.hashCode());
		assertEquals(
				"Two GameTime instances with same constructors do not have same hash code.",
				gm1.hashCode(), gm2.hashCode());
		assertEquals(
				"Two GameTime instances with different constructors do not have same hash code.",
				gm1.hashCode(), gm3.hashCode());
		assertEquals(
				"Two zeroed GameTime instances do not have same hash code.",
				new GameTime().hashCode(), new GameTime(0).hashCode());
	}

	/**
	 * Tests getter for hour field of random <code>GameTime</code> instance.
	 * Test method for
	 * {@link org.freenono.model.game_modes.GameTime#getMinutes()}.
	 */
	@Test
	public final void testGetMinutes() {

		randomTime1.getMinutes();
		randomTime2.getMinutes();
	}

	/**
	 * Tests getter for hour field of random <code>GameTime</code> instance.
	 * Test method for
	 * {@link org.freenono.model.game_modes.GameTime#getSeconds()}.
	 */
	@Test
	public final void testGetSeconds() {

		randomTime1.getSeconds();
		randomTime2.getSeconds();
	}

	/**
	 * Tests getter for hour field of random <code>GameTime</code> instance.
	 * Test method for {@link org.freenono.model.game_modes.GameTime#getHours()}
	 * .
	 */
	@Test
	public final void testGetHours() {

		randomTime1.getHours();
		randomTime2.getHours();
	}

}
