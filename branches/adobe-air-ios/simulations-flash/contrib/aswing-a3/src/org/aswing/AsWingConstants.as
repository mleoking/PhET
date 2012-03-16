/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing
{
	
/**
 * A collection of constants generally used for positioning and orienting
 * components on the screen.
 * 
 * @author iiley
 */
public class AsWingConstants{
		
		
		public static const NONE:int = -1;

        /** 
         * The central position in an area. Used for
         * both compass-direction constants (NORTH, etc.)
         * and box-orientation constants (TOP, etc.).
         */
        public static const CENTER:int  = 0;

        // 
        // Box-orientation constant used to specify locations in a box.
        //
        /** 
         * Box-orientation constant used to specify the top of a box.
         */
        public static const TOP:int     = 1;
        /** 
         * Box-orientation constant used to specify the left side of a box.
         */
        public static const LEFT:int    = 2;
        /** 
         * Box-orientation constant used to specify the bottom of a box.
         */
        public static const BOTTOM:int  = 3;
        /** 
         * Box-orientation constant used to specify the right side of a box.
         */
        public static const RIGHT:int   = 4;

        // 
        // Compass-direction constants used to specify a position.
        //
        /** 
         * Compass-direction North (up).
         */
        public static const NORTH:int      = 1;
        /** 
         * Compass-direction north-east (upper right).
         */
        public static const NORTH_EAST:int = 2;
        /** 
         * Compass-direction east (right).
         */
        public static const EAST:int       = 3;
        /** 
         * Compass-direction south-east (lower right).
         */
        public static const SOUTH_EAST:int = 4;
        /** 
         * Compass-direction south (down).
         */
        public static const SOUTH:int      = 5;
        /** 
         * Compass-direction south-west (lower left).
         */
        public static const SOUTH_WEST:int = 6;
        /** 
         * Compass-direction west (left).
         */
        public static const WEST:int       = 7;
        /** 
         * Compass-direction north west (upper left).
         */
        public static const NORTH_WEST:int = 8;

        //
        // These constants specify a horizontal or 
        // vertical orientation. For example, they are
        // used by scrollbars and sliders.
        //
        /** 
         * Horizontal orientation. Used for scrollbars and sliders.
         */
        public static const HORIZONTAL:int = 0;
        /** 
         * Vertical orientation. Used for scrollbars and sliders.
         */
        public static const VERTICAL:int   = 1;
}
}