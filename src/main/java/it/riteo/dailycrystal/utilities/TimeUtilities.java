/*
 * DailyCrystal
 * Copyright (C) 2021  Riteo Siuga
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package it.riteo.dailycrystal.utilities;

/**
 * An utility class capable of managing time in milliseconds.
 */
public class TimeUtilities {
	/* 1000 millis * 60 seconds * 60 minutes * 24 hours = 86400000 */
	public static final long MILLISECONDS_IN_A_DAY = 86400000;

	/* These are static utilities, we don't want to be able to instance them. */
	private TimeUtilities() {
	}

	/**
	 * Gets the time of midnight in milliseconds.
	 *
	 * @param milliseconds         - the time in milliseconds of the day from where
	 *                             to get the time of midnight.
	 * @param offsetMillisTimeZone - the time zone offset in milliseconds.
	 * @return the time of midnight in milliseconds.
	 */
	public static long getMidnightMillis(long milliseconds, long offsetMillisTimeZone) {
		long millisecondsSinceMidnight = (milliseconds + offsetMillisTimeZone) % MILLISECONDS_IN_A_DAY;
		return milliseconds - millisecondsSinceMidnight;
	}

	/**
	 * Converts a number in milliseconds to ticks
	 *
	 * @param milliseconds - the amount in milliseconds to convert to ticks.
	 * @return the number of ticks equivalent to the amount of milliseconds
	 *         specified.
	 */
	public static long millisToTicks(long milliseconds) {
		return milliseconds / 1000 * 20;
	}

	/**
	 * Converts a number in ticks to milliseconds
	 *
	 * @param ticks - the number of ticks to convert to milliseconds.
	 * @return the amount of milliseconds equivalent to the amount of ticks
	 *         specified.
	 */
	public static long ticksToMillis(long ticks) {
		return ticks / 20 * 1000;
	}
}
