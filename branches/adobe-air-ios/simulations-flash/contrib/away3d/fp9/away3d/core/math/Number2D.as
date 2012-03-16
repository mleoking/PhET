package away3d.core.math
{
    /** A point in 2D space. */
    public final class Number2D
    {
        /** Horizontal coordinate. */
        public var x:Number;

        /** Vertical coordinate. */
        public var y:Number;

        public function Number2D(x:Number = 0, y:Number = 0)
        {
            this.x = x;
            this.y = y;
        }

        public function clone():Number2D
        {
            return new Number2D(x, y);
        }

        public function get modulo():Number
        {
            return Math.sqrt(x*x + y*y);
        }

        public static function scale(v:Number2D, s:Number):Number2D
        {
            return new Number2D
            (
                v.x * s,
                v.y * s
            );
        }

        public static function add(v:Number3D, w:Number3D):Number2D
        {
            return new Number2D
            (
                v.x + w.x,
                v.y + w.y
           );
        }

        public static function sub(v:Number2D, w:Number2D):Number2D
        {
            return new Number2D
            (
                v.x - w.x,
                v.y - w.y
           );
        }

        public static function dot(v:Number2D, w:Number2D):Number
        {
            return (v.x * w.x + v.y * w.y);
        }

        public function normalize():void
        {
            var mod:Number = modulo;

            if (mod != 0 && mod != 1)
            {
                this.x /= mod;
                this.y /= mod;
            }
        }
		
        // Relative directions.
        public static var LEFT    :Number2D = new Number2D(-1,  0);
        public static var RIGHT   :Number2D = new Number2D( 1,  0);
        public static var UP      :Number2D = new Number2D( 0,  1);
        public static var DOWN    :Number2D = new Number2D( 0, -1);

        public function toString(): String
        {
            return 'x:' + x + ' y:' + y;
        }
    }
}