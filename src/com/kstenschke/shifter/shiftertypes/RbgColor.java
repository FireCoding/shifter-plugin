/*
 * Copyright 2011-2014 Kay Stenschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kstenschke.shifter.shiftertypes;

import java.awt.*;

/**
 * RGB color class
 */
public class RbgColor {

	/**
	 * Constructor
	 */
	public RbgColor() {

	}

	/**
	 * Check whether given string represents a hex RGB color, prefix must be "#"
	 *
	 * @param	str			String to be checked
	 * @param	prefix		Character found to precede that string
	 * @return	boolean.
	 */
	public boolean isRgbColorString(String str, String prefix) {
		return !( !prefix.equals("#")  || !(str.matches("[0-9a-fA-F]{3}") || str.matches("[0-9a-fA-F]{6}")) );
	}

	/**
	 * @param	rgbStr		String representing an RGB color
	 * @param	isUp		Shifting up or down?
	 * @return	String
	 */
	public String getShifted(String rgbStr, boolean isUp) {
		if (rgbStr.length() == 3) {
			rgbStr = sixfoldTripleColor(rgbStr);
		}

		if(isUp) {
			if (!isWhite(rgbStr))   return lightenRgbString(rgbStr);
		} else if(!isBlack(rgbStr)) return darkenRgbString(rgbStr);

		return rgbStr;
	}

	/**
	 * Check whether given String represents RGB white (fff, FFF, ffffff, FFFFFF)
	 *
	 * @param	str			RBG color string to be checked
	 * @return	boolean.
	 */
	private static boolean isWhite(String str) {
		return str.equalsIgnoreCase("fff") || str.equalsIgnoreCase("ffffff");
	}

	/**
	 * Check whether given String represents RGB black (000, 000000)
	 *
	 * @param	str			RGB color string
	 * @return	boolean.
	 */
	private static boolean isBlack(String str) {
		return str.equalsIgnoreCase("000") || str.equalsIgnoreCase("000000");
	}

	/**
	 * Convert three digit color string into six digits
	 *
	 * @param	rgbStr		RGB color string
	 * @return	String
	 */
	private static String sixfoldTripleColor(String rgbStr) {
		String R = rgbStr.substring(0, 1);
		String G = rgbStr.substring(1, 2);
		String B = rgbStr.substring(2, 3);

		return "".concat(R).concat(R).concat(G).concat(G).concat(B).concat(B);
	}

	/**
	 * Get Color object of given RGB string
	 *
	 * @param	rgbStr	String representing an RGB color
	 * @return			Color object
	 */
	private static Color getColorFromRgbString(String rgbStr) {
		int R = Integer.parseInt(rgbStr.substring(0, 2), 16);
		int G = Integer.parseInt(rgbStr.substring(2, 4), 16);
		int B = Integer.parseInt(rgbStr.substring(4, 6), 16);

		return new Color(R, G, B);
	}

	/**
	 * Shift given RGB color hex string to be lighter
	 *
	 * @param	rgbStr		String of RGB color
	 * @return	String
	 */
	private static String lightenRgbString(String rgbStr) {
		Color lighterColor = lighter(getColorFromRgbString(rgbStr));

		return getHexFromColor(lighterColor);
	}

	/**
	 * Shift given RGB color hex string to be darker
	 *
	 * @param	rgbStr		RGB color string
	 * @return	String
	 */
	private static String darkenRgbString(String rgbStr) {
		Color darkerColor = darker(getColorFromRgbString(rgbStr));

		return getHexFromColor(darkerColor);
	}

	/**
	 * Return the hex name of a specified color.
	 *
	 * @param	color	Color to get hex name of.
	 * @return			Hex name of color: "rrggbb".
	 */
	private static String getHexFromColor(Color color) {
		String rHex = Integer.toString(color.getRed(), 16);
		String gHex = Integer.toString(color.getGreen(), 16);
		String bHex = Integer.toString(color.getBlue(), 16);

		return (rHex.length() == 2 ? "" + rHex : "0" + rHex) + (gHex.length() == 2 ? "" + gHex : "0" + gHex) +  (bHex.length() == 2 ? "" + bHex : "0" + bHex);
	}

	/**
	 * Darken given color.
	 *
	 * @param	color	Color to make darker
	 * @return			Darker color.
	 */
	private static Color darker(Color color) {
        return addToRGB(color, -1);
	}

	/**
	 * Lighten given color.
	 *
	 * @param	color		Color to be made lighter.
	 * @return	Color		Lighter color.
	 */
	private static Color lighter(Color color) {
        return addToRGB(color, 1);
	}

    /**
     * Increment RGB values of given color by given amount
     *
     * @param   color
     * @param   amount
     * @return  Color
     */
    private static Color addToRGB(Color color, int amount) {
        int red = Math.round(color.getRed() + amount);
        int green = Math.round(color.getGreen() + amount);
        int blue = Math.round(color.getBlue() + amount);

        red     = red < 0 ? 0 : red > 255 ? 255 : red;
        green   = green < 0 ? 0 : green > 255 ? 255 : green;
        blue    = blue < 0 ? 0 : blue > 255 ? 255 : blue;

        return new Color(red, green, blue, color.getAlpha());
    }

}