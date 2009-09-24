package away3d.core.block
{
    import away3d.core.draw.*;
    import away3d.core.geom.*;
    import away3d.core.render.*;
    import away3d.core.utils.*;
    
    import flash.display.Graphics;
    import flash.utils.*;

    /**
    * Convex hull primitive that blocks all primitives behind and contained completely inside.
    */
    public class ConvexBlocker extends Blocker
    {
        private var _boundlines:Array;
        
		/**
		 * Defines the vertices used to calulate the convex hull.
		 */
		public var vertices:Array;
        
		/**
		 * @inheritDoc
		 */
		public override function calc():void
		{	
			_boundlines = [];
            screenZ = 0;
            maxX = -Infinity;
            maxY = -Infinity;
            minX = Infinity;
            minY = Infinity;
            
            var _length:int = vertices.length/3;
            for (var i:int = 0; i < _length; ++i)
            {
                var vx:Number = vertices[i];
                var vy:Number = vertices[i+1];
                var vz:Number = vertices[i+2];
                var next:int = ((i+3) % _length);
                _boundlines.push(Line2D.from2points(vx, vy, vertices[next], vertices[next+1]));
                if (screenZ < vz)
                    screenZ = vz;
                if (minX > vx)
                    minX = vx;
                if (maxX < vx)
                    maxX = vx;
                if (minY > vy)
                    minY = vy;
                if (maxY < vy)
                    maxY = vy;
            }
            maxZ = screenZ;
            minZ = screenZ;
		}
        
		/**
		 * @inheritDoc
		 */
        public override function contains(x:Number, y:Number):Boolean
        {   
            for each (var boundline:Line2D in _boundlines)
                if (boundline.side(x, y) < 0)
                    return false;
            return true;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function block(pri:DrawPrimitive):Boolean
        {
            if (pri is DrawTriangle)
            {
                var tri:DrawTriangle = pri as DrawTriangle;
                return contains(tri.v0x, tri.v0y) && contains(tri.v1x, tri.v1y) && contains(tri.v2x, tri.v2y);
            }
            return contains(pri.minX, pri.minY) && contains(pri.minX, pri.maxY) && contains(pri.maxX, pri.maxY) && contains(pri.maxX, pri.minY);
        }
        
		/**
		 * @inheritDoc
		 */
        public override function render():void
        {
            var graphics:Graphics = source.session.graphics;
            graphics.lineStyle(2, Color.fromHSV(0, 0, (Math.sin(getTimer()/1000)+1)/2));
            var _length:int = _boundlines.length;
            for (var i:int = 0; i < _length; ++i)
            {
                var line:Line2D = _boundlines[i];
                var prev:Line2D = _boundlines[(i-1+_length) % _length];
                var next:Line2D = _boundlines[(i+1+_length) % _length];

                var a:ScreenVertex = Line2D.cross(prev, line);
                var b:ScreenVertex = Line2D.cross(line, next);

                graphics.moveTo(a.x, a.y);
                graphics.lineTo(b.x, b.y);
                graphics.moveTo(a.x, a.y);
            }

            var count:int = (maxX - minX) * (maxY - minY) / 2000;
            if (count > 50)
                count = 50;
            for (var k:int = 0; k < count; ++k)
            {
                var x:Number = minX + (maxX - minX)*Math.random();
                var y:Number = minY + (maxY - minY)*Math.random();
                if (contains(x, y))
                {
                    graphics.lineStyle(1, Color.fromHSV(0, 0, Math.random()));
                    graphics.drawCircle(x, y, 3);
                }
            }
        }
    }
}
