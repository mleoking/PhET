package away3d.core.draw
{
    import away3d.core.base.*;

    /**
    * representation of a 3d vertex resolved to the view.
    */
    public final class ScreenVertex
    {
		private var persp:Number;
		private var faz:Number;
		private var fbz:Number;
		private var ifmz2:Number;
		private var mx2:Number;
		private var my2:Number;
		private var dx:Number;
		private var dy:Number;

		public var vectorInstructionType:String = VectorInstructionType.LINE;
		
    	/**
    	 * The view x position of the vertex in the view.
    	 */
        public var vx:Number;
        
    	/**
    	 * The view y position of the vertex in the view.
    	 */
        public var vy:Number;
		
    	/**
    	 * The screen x position of the vertex in the view.
    	 */
        public var x:Number;
        
    	/**
    	 * The screen y position of the vertex in the view.
    	 */
        public var y:Number;
        
    	/**
    	 * The screen z position of the vertex in the view.
    	 */
        public var z:Number;
        
        /**
        * Indicates whether the vertex is visible after projection.
        */
        public var visible:Boolean;
    	
    	public var viewTimer:int;
    	
		/**
		 * Creates a new <code>PrimitiveQuadrantTreeNode</code> object.
		 *
		 * @param	x	[optional]		The x position of the vertex in the view. Defaults to 0.
		 * @param	y	[optional]		The y position of the vertex in the view. Defaults to 0.
		 * @param	z	[optional]		The z position of the vertex in the view. Defaults to 0.
		 */
        public function ScreenVertex(x:Number = 0, y:Number = 0, z:Number = 0)
        {
            this.x = x;
            this.y = y;
            this.z = z;
    
            this.visible = false;
        }
		
		/**
		 * Used to trace the values of a vertex.
		 * 
		 * @return A string representation of the vertex object.
		 */
        public function toString(): String
        {
            return "new ScreenVertex("+x+', '+y+', '+z+")";
        }
		
		/**
		 * Calculates the squared distance between two screen vertex objects.
		 * 
		 * @param	b	The screen vertex object to use for the calcation.
		 * @return		The squared scalar value of the vector between this and the given scren vertex.
		 */
        public static function distanceSqr(ax:Number, ay:Number, bx:Number, by:Number):Number
        {
            return (ax - bx)*(ax - bx) + (ay - by)*(ay - by);
        }
		
		/**
		 * Calculates the distance between two screen vertex objects.
		 * 
		 * @param	b	The second screen vertex object to use for the calcation.
		 * @return		The scalar value of the vector between this and the given screen vertex.
		 */
        public function distance(b:ScreenVertex):Number
        {
            return Math.sqrt((x - b.x)*(x - b.x) + (y - b.y)*(y - b.y));
        }
		
		/**
		 * Calculates affine distortion present at the midpoint between two screen vertex objects.
		 * 
		 * @param	b		The second screen vertex object to use for the calcation.
		 * @param	focus	The focus value used for the distortion calulations. 
		 * @return			The scalar value of the vector between this and the given screen vertex.
		 */
        public function distortSqr(b:ScreenVertex, focus:Number):Number
        {
            faz = focus + z;
            fbz = focus + z;
            ifmz2 = 2 / (faz + fbz);
            mx2 = (x*faz + b.x*fbz)*ifmz2;
            my2 = (y*faz + b.y*fbz)*ifmz2;

            dx = x + b.x - mx2;
            dy = y + b.y - my2;

            return 50*(dx*dx + dy+dy); // (distort*10)^2
        }
		
		/**
		 * Returns a screen vertex with values given by a weighted mean calculation.
		 * 
		 * @param	a		The first screen vertex to use for the calculation.
		 * @param	b		The second screen vertex to use for the calculation.
		 * @param	aw		The first screen vertex weighting.
		 * @param	bw		The second screen vertex weighting.
		 * @param	focus	The focus value used for the weighting calulations.
		 * @return			The resulting screen vertex.
		 */
        public static function weighted(a:ScreenVertex, b:ScreenVertex, aw:Number, bw:Number, focus:Number):ScreenVertex
        {
            if ((bw == 0) && (aw == 0))
                throw new Error("Zero weights");

            if (bw == 0)
                return new ScreenVertex(a.x, a.y, a.z);
            else
            if (aw == 0)
                return new ScreenVertex(b.x, b.y, b.z);

            var d:Number = aw + bw;
            var ak:Number = aw / d;
            var bk:Number = bw / d;

            var x:Number = a.x*ak + b.x*bk;
            var y:Number = a.y*ak + b.y*bk;

            var azf:Number = a.z / focus;
            var bzf:Number = b.z / focus;

            var faz:Number = 1 + azf;
            var fbz:Number = 1 + bzf;

            var axf:Number = a.x*faz - x*azf;
            var bxf:Number = b.x*fbz - x*bzf;
            var ayf:Number = a.y*faz - y*azf;
            var byf:Number = b.y*fbz - y*bzf;

            var det:Number = axf*byf - bxf*ayf;
            var da:Number = x*byf - bxf*y;
            var db:Number = axf*y - x*ayf;

            return new ScreenVertex(x, y, (da*a.z + db*b.z) / det);
        }
		
		/**
		 * Creates the median screen vertex between the two given screen vertex objects.
		 * 
		 * @param	a					The index of the first screen vertex to use for the calculation.
		 * @param	b					The index of the second screen vertex to use for the calculation.
		 * @param	screenVertices		The Array of screen vertices to use for the calculation.
		 * @param	screenIndices		The Array of screen indices to use for the calculation.
		 * @param	focus				The focus value used for the median calulations.
		 */
        public static function median(aindex:Number, bindex:Number, screenVertices:Array, screenIndices:Array, focus:Number):void
        {
        	var avertex:int = screenIndices[aindex]*3;
        	var ax:Number = screenVertices[avertex];
        	var ay:Number = screenVertices[avertex+1];
        	var az:Number = screenVertices[avertex+2];
        	
        	var bvertex:int = screenIndices[bindex]*3;
        	var bx:Number = screenVertices[bvertex];
        	var by:Number = screenVertices[bvertex+1];
        	var bz:Number = screenVertices[bvertex+2];
        	
            var mz:Number = (az + bz) / 2;
			
            var faz:Number = focus + az;
            var fbz:Number = focus + bz;
            var ifmz:Number = 1 / (focus + mz) / 2;
			
			screenVertices[screenVertices.length] = (ax*faz + bx*fbz)*ifmz;
			screenVertices[screenVertices.length] = (ay*faz + by*fbz)*ifmz;
			screenVertices[screenVertices.length] = mz;
        }
    }
}
