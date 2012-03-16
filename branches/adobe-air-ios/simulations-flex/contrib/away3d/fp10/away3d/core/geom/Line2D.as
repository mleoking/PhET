package away3d.core.geom
{
	import away3d.core.draw.*;
	

    /** Line in 2D space */
    public class Line2D
    {
        public var a:Number;
    
        public var b:Number;
    
        public var c:Number;

        public function Line2D(a:Number, b:Number, c:Number)
        {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public static function from2points(v0x:Number, v0y:Number, v1x:Number, v1y:Number):Line2D
        {
            var a:Number = v1y - v0y;
            var b:Number = v0x - v1x;
            var c:Number = -(b*v0y + a*v0x);

            return new Line2D(a, b, c);
        }

        public static function cross(u:Line2D, v:Line2D):ScreenVertex
        {
            var det:Number = u.a*v.b - u.b*v.a;
            var xd:Number = u.b*v.c - u.c*v.b;
            var yd:Number = v.a*u.c - u.a*v.c;

            return new ScreenVertex(xd / det, yd / det, 0);
        }

        public function sideV(v:ScreenVertex):Number
        {
            return a*v.x + b*v.y + c;
        }

        public function side(x:Number, y:Number):Number
        {
            return a*x + b*y + c;
        }

        public function distance(v:ScreenVertex):Number
        {
            return sideV(v) / Math.sqrt(a*a + b*b);
        }

        public function toString():String
        {
            return "line{ a: "+a+" b: "+b+" c:"+c+" }";
        }
    }
}
