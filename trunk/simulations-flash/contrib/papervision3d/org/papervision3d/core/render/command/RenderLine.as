package org.papervision3d.core.render.command
{
	
	/**
	 * @Author Ralph Hauwert
	 */
	 
	import flash.display.Graphics;
	import flash.geom.Point;
	
	import org.papervision3d.core.geom.renderables.Line3D;
	import org.papervision3d.core.geom.renderables.Vertex3DInstance;
	import org.papervision3d.core.math.Number2D;
	import org.papervision3d.core.math.Number3D;
	import org.papervision3d.core.render.data.RenderHitData;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.materials.special.LineMaterial;	
	
	public class RenderLine extends RenderableListItem implements IRenderListItem
	{
		
		public var line:Line3D;
		public var renderer:LineMaterial;
		
		// rather that create and clean up vector objects, we'll store all our temp working vectors as statics
		private static var lineVector:Number3D = Number3D.ZERO; 
		private static var mouseVector:Number3D = Number3D.ZERO;
		
		//premade Number2Ds for hittest function		private var p : Number2D;
		private var l1 : Number2D;
		private var l2 : Number2D;
		private var v : Number2D;
		private var cp3d : Number3D; 

		public var v0:Vertex3DInstance;
        public var v1:Vertex3DInstance;
        public var cV:Vertex3DInstance;
		
		public var size:Number;
		public var length:Number;

		public function RenderLine(line:Line3D)
		{
			super();
			this.renderable = Line3D;
			this.renderableInstance = line;
			this.line = line;
			this.instance = line.instance;
			
			v0 = line.v0.vertex3DInstance;
			v1 = line.v1.vertex3DInstance;
			cV = line.cV.vertex3DInstance;
			
			// pre-made for hittest
			p = new Number2D(); 
			l1 = new Number2D(); 
			l2 = new Number2D(); 
			v = new Number2D(); 	
			cp3d = new Number3D(); 		
			
		}
		
		override public function render(renderSessionData:RenderSessionData, graphics:Graphics):void
		{

			renderer.drawLine(this, graphics, renderSessionData);
			
		}
		
		override public function hitTestPoint2D(point:Point, rhd:RenderHitData):RenderHitData
		{
			if(renderer.interactive)
			{
				var linewidth:Number = line.size; 
				
				p.reset(point.x, point.y); 
				
				l1.reset(line.v0.vertex3DInstance.x, line.v0.vertex3DInstance.y);
				l2.reset(line.v1.vertex3DInstance.x, line.v1.vertex3DInstance.y);
		
				// get the vector for the line
				v.copyFrom(l2); 
				v.minusEq(l1); 
				
				// magic formula for calculating how how far along the line a perpendicular 
				// coming from the line to the point would hit. If this number is between 0 and 1 
				// the point is closest to a part of the line that exists.
				var u:Number = (((p.x - l1.x)*(l2.x - l1.x)) + ((p.y-l1.y)*(l2.y - l1.y)))/((v.x*v.x)+(v.y*v.y)); 
				
				if((u>0)&&(u<1)) 
				{
				
					// so then to work out that collision point multiply v by u and add it to l1
					v.multiplyEq(u); 
					v.plusEq(l1); 
					
					// then get the vector between the collision point and the mousepoint
					v.minusEq(p); 
					//var dist:Number2D = Number2D.subtract(cp, p); 
					
					// and get the magnitude of that distance vector, squared
					var d:Number =  (v.x*v.x)+(v.y*v.y);
					
					// and if it's less than the linewidth squared we have a hit
					if(d<(linewidth*linewidth))
					{
						rhd.displayObject3D = line.instance; 
						rhd.material = renderer;
						rhd.renderable = line; 
						rhd.hasHit = true;
						
						//TODO UPDATE 3D hit point and UV
						// currently we're just moving u along the 3D line, but this isn't accurate.
						cp3d.reset(line.v1.x-line.v0.x, line.v1.y-line.v0.y, line.v1.x-line.v0.x);
						cp3d.x*=u; 
						cp3d.y*=u; 
						cp3d.z*=u; 
						cp3d.x+=line.v0.x; 
						cp3d.y+=line.v0.y; 
						cp3d.z+=line.v0.z; 
						
						rhd.x = cp3d.x; 
						rhd.y = cp3d.y; 
						rhd.z = cp3d.z; 
						rhd.u = 0;
						rhd.v = 0; 
						return rhd; 
					}
					
				}
			}
			return rhd;
		}
		
		
		override public function getZ(x:Number, y:Number, focus:Number):Number{
              
            ax = v0.x;
            ay = v0.y;
            az = v0.z;
            bx = v1.x;
            by = v1.y;
            bz = v1.z;

            if ((ax == x) && (ay == y))
                return az;

            if ((bx == x) && (by == y))
                return bz;

            dx = bx - ax;
            dy = by - ay;

            azf = az / focus;
            bzf = bz / focus;

            faz = 1 + azf;
            fbz = 1 + bzf;

            xfocus = x;
            yfocus = y;

            axf = ax*faz - x*azf;
            bxf = bx*fbz - x*bzf;
            ayf = ay*faz - y*azf;
            byf = by*fbz - y*bzf;

            det = dx*(axf - bxf) + dy*(ayf - byf);
            db = dx*(axf - x) + dy*(ayf - y);
            da = dx*(x - bxf) + dy*(y - byf);

            return (da*az + db*bz) / det;
		}
		
		
		/*
		Quad Vars Don't Touch
		*/
		private var ax:Number;
        private var ay:Number;
        private var az:Number;
        private var bx:Number;
        private var by:Number;
        private var bz:Number;
        private var dx:Number;
        private var dy:Number;
        private var azf:Number;
        private var bzf:Number;
        private var faz:Number;
        private var fbz:Number;
        private var xfocus:Number;
        private var yfocus:Number;
        private var axf:Number;
        private var bxf:Number;
        private var ayf:Number;
        private var byf:Number;
        private var det:Number;
        private var db:Number;
        private var da:Number;
        
       
		
	}
}