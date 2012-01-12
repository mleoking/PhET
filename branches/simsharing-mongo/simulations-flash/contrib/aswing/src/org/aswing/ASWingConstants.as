/*
 Copyright aswing.org, see the LICENCE.txt.
*/
/**
 * A collection of constants generally used for positioning and orienting
 * components on the screen.
 * 
 * @author iiley
 */
class org.aswing.ASWingConstants{
	
		public static var NONE:Number = -1;

        /** 
         * The central position in an area. Used for
         * both compass-direction constants (NORTH, etc.)
         * and box-orientation constants (TOP, etc.).
         */
        public static var CENTER:Number  = 0;

        // 
        // Box-orientation constant used to specify locations in a box.
        //
        /** 
         * Box-orientation constant used to specify the top of a box.
         */
        public static var TOP:Number     = 1;
        /** 
         * Box-orientation constant used to specify the left side of a box.
         */
        public static var LEFT:Number    = 2;
        /** 
         * Box-orientation constant used to specify the bottom of a box.
         */
        public static var BOTTOM:Number  = 3;
        /** 
         * Box-orientation constant used to specify the right side of a box.
         */
        public static var RIGHT:Number   = 4;

        // 
        // Compass-direction constants used to specify a position.
        //
        /** 
         * Compass-direction North (up).
         */
        public static var NORTH:Number      = 1;
        /** 
         * Compass-direction north-east (upper right).
         */
        public static var NORTH_EAST:Number = 2;
        /** 
         * Compass-direction east (right).
         */
        public static var EAST:Number       = 3;
        /** 
         * Compass-direction south-east (lower right).
         */
        public static var SOUTH_EAST:Number = 4;
        /** 
         * Compass-direction south (down).
         */
        public static var SOUTH:Number      = 5;
        /** 
         * Compass-direction south-west (lower left).
         */
        public static var SOUTH_WEST:Number = 6;
        /** 
         * Compass-direction west (left).
         */
        public static var WEST:Number       = 7;
        /** 
         * Compass-direction north west (upper left).
         */
        public static var NORTH_WEST:Number = 8;

        //
        // These constants specify a horizontal or 
        // vertical orientation. For example, they are
        // used by scrollbars and sliders.
        //
        /** 
         * Horizontal orientation. Used for scrollbars and sliders.
         */
        public static var HORIZONTAL:Number = 0;
        /** 
         * Vertical orientation. Used for scrollbars and sliders.
         */
        public static var VERTICAL:Number   = 1;
}
