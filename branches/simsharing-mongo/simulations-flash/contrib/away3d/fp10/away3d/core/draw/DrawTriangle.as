package away3d.core.draw
{
    import away3d.arcane;
    import away3d.core.base.*;
    import away3d.core.utils.FaceVO;
    import away3d.materials.*;
    
    import flash.geom.Matrix;
    
    use namespace arcane;
    
    /**
    * Triangle drawing primitive
    */
    public class DrawTriangle extends DrawPrimitive
    {
		/** @private */
        arcane function fivepointcut(i0:Number, v01x:Number, v01y:Number, v01z:Number, i1:Number, v12x:Number, v12y:Number, v12z:Number, i2:Number, uv0:UV, uv01:UV, uv1:UV, uv12:UV, uv2:UV):Array
        {
        	var vertexIndex:int = screenVertices.length/3;
        	var v0:int = screenIndices[i0];
        	var v1:int = screenIndices[i1];
        	var v2:int = screenIndices[i2];
        	
            if (ScreenVertex.distanceSqr(screenVertices[v0*3], screenVertices[v0*3+1], v12x, v12y) < ScreenVertex.distanceSqr(v01x, v01y, screenVertices[v2*3], screenVertices[v2*3+1]))
            {
            	var index0:int = screenIndices.length;
	        	screenIndices[screenIndices.length] = v0;
	        	screenIndices[screenIndices.length] = vertexIndex;
	        	screenIndices[screenIndices.length] = vertexIndex+1;
	        	var index1:int = screenIndices.length;
	        	screenIndices[screenIndices.length] = vertexIndex;
	        	screenIndices[screenIndices.length] = v1;
	        	screenIndices[screenIndices.length] = vertexIndex+1;
	        	var index2:int = screenIndices.length;
	        	screenIndices[screenIndices.length] = v0;
	        	screenIndices[screenIndices.length] = vertexIndex+1;
	        	screenIndices[screenIndices.length] = v2;
	        	var index3:int = screenIndices.length;
	        	
	        	screenVertices[screenVertices.length] = v01x;
				screenVertices[screenVertices.length] = v01y;
				screenVertices[screenVertices.length] = v01z;
				
	        	screenVertices[screenVertices.length] = v12x;
				screenVertices[screenVertices.length] = v12y;
				screenVertices[screenVertices.length] = v12z;
				
	            return [
	                create(source, faceVO, material, screenVertices, screenIndices, screenCommands, index0, index1, uv0,  uv01, uv12, true),
	                create(source, faceVO, material, screenVertices, screenIndices, screenCommands, index1, index2, uv01,  uv1, uv12, true),
	                create(source, faceVO, material, screenVertices, screenIndices, screenCommands, index2, index3, uv0,  uv12, uv2, true)
	            ];
            }
            else
            {
            	var index4:int = screenIndices.length;
	        	screenIndices[screenIndices.length] = v0;
	        	screenIndices[screenIndices.length] = vertexIndex;
	        	screenIndices[screenIndices.length] = v2;
	        	var index5:int = screenIndices.length;
	        	screenIndices[screenIndices.length] = vertexIndex;
	        	screenIndices[screenIndices.length] = v1;
	        	screenIndices[screenIndices.length] = vertexIndex+1;
	        	var index6:int = screenIndices.length;
	        	screenIndices[screenIndices.length] = vertexIndex;
	        	screenIndices[screenIndices.length] = vertexIndex+1;
	        	screenIndices[screenIndices.length] = v2;
	        	var index7:int = screenIndices.length;
	        	
	        	screenVertices[screenVertices.length] = v01x;
				screenVertices[screenVertices.length] = v01y;
				screenVertices[screenVertices.length] = v01z;
				
	        	screenVertices[screenVertices.length] = v12x;
				screenVertices[screenVertices.length] = v12y;
				screenVertices[screenVertices.length] = v12z;
	        	
	            return [
	                create(source, faceVO, material, screenVertices, screenIndices, screenCommands, index4, index5, uv0,  uv01, uv2, true),
	                create(source, faceVO, material, screenVertices, screenIndices, screenCommands, index5, index6, uv01,  uv1, uv12, true),
	                create(source, faceVO, material, screenVertices, screenIndices, screenCommands, index6, index7, uv01,  uv12, uv2, true)
	            ];
            }
        }
        
        private var materialWidth:Number;
        private var materialHeight:Number;
        private var _u0:Number;
        private var _u1:Number;
        private var _u2:Number;
        private var _v0:Number;
        private var _v1:Number;
        private var _v2:Number;
        private var _areaSign:Number;
        private var focus:Number;
        private var ax:Number;
        private var ay:Number;
        private var az:Number;
        private var bx:Number;
        private var by:Number;
        private var bz:Number;
        private var cx:Number;
        private var cy:Number;
        private var cz:Number;
        private var azf:Number;
        private var bzf:Number;
        private var czf:Number;
        private var faz:Number;
        private var fbz:Number;
        private var fcz:Number;
        private var axf:Number;
        private var bxf:Number;
        private var cxf:Number;
        private var ayf:Number;
        private var byf:Number;
        private var cyf:Number;
        private var det:Number;
        private var da:Number;
        private var db:Number;
        private var dc:Number;
		private var au:Number;
        private var av:Number;
        private var bu:Number;
        private var bv:Number;
        private var cu:Number;
        private var cv:Number;
        private var uv01:UV;
        private var uv12:UV;
        private var uv20:UV;
        private var _invtexmapping:Matrix = new Matrix();
        private var _index:int;
        private var _vertex:int;
        private var _x:Number;
        private var _y:Number;
        private var _z:Number;
        private var _vertexCount:uint;
        
        private function num(n:Number):Number
        {
            return int(n*1000)/1000;
        }
        
		/**
		 * The x position of the v0 screenvertex of the triangle primitive.
		 */
        public var v0x:Number;
        
		/**
		 * The y position of the v0 screenvertex of the triangle primitive.
		 */
        public var v0y:Number;
        
		/**
		 * The z position of the v0 screenvertex of the triangle primitive.
		 */
        public var v0z:Number;
        
		/**
		 * The x position of the v1 screenvertex of the triangle primitive.
		 */
        public var v1x:Number;
        
		/**
		 * The y position of the v1 screenvertex of the triangle primitive.
		 */
        public var v1y:Number;
        
		/**
		 * The z position of the v1 screenvertex of the triangle primitive.
		 */
        public var v1z:Number;
        
		/**
		 * The x position of the v2 screenvertex of the triangle primitive.
		 */
        public var v2x:Number;
        
		/**
		 * The y position of the v2 screenvertex of the triangle primitive.
		 */
        public var v2y:Number;
        
		/**
		 * The z position of the v2 screenvertex of the triangle primitive.
		 */
        public var v2z:Number;
        
		/**
		 * The uv0 uv coordinate of the triangle primitive.
		 */
        public var uv0:UV;
        
		/**
		 * The uv1 uv coordinate of the triangle primitive.
		 */
        public var uv1:UV;
        
		/**
		 * The uv2 uv coordinate of the triangle primitive.
		 */
        public var uv2:UV;
        
        //public var vertices:Vector.<Number> = new Vector.<Number>();
        
        public var uvtData:Vector.<Number> = new Vector.<Number>();
        
		/**
		 * The calulated area of the triangle primitive.
		 */
        public var area:Number;
        
    	/**
    	 * A reference to the face value object used by the triangle primitive.
    	 */
        public var faceVO:FaceVO;
        
    	/**
    	 * Indicates whether the face of the triangle primitive is facing away from the camera.
    	 */
        public var backface:Boolean = false;
        
    	/**
    	 * The material object used as the triangle primitive's texture.
    	 */
        public var material:ITriangleMaterial;
        
        public var screenVertices:Array;
        
        public var screenIndices:Array;
        
        public var screenCommands:Array;
        
        public var startIndex:int;
        
        public var endIndex:int;
        
        public var reverseArea:Boolean;
        
		/**
		 * @inheritDoc
		 */
        public override function clear():void
        {
            //v0 = null;
            //v1 = null;
            //v2 = null;
            uv0 = null;
            uv1 = null;
            uv2 = null;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function render():void
        {
            material.renderTriangle(this);
        }
        
        /**
        * Calculates from the uv coordinates the mapping matrix required to draw the triangle primitive.
        */
        public final function transformUV(material:IUVMaterial):Matrix
        {
            materialWidth = material.width,
            materialHeight = material.height;
            
            if (uv0 == null || uv1 == null || uv2 == null)
                return null;

            _u0 = materialWidth * uv0._u;
            _u1 = materialWidth * uv1._u;
            _u2 = materialWidth * uv2._u;
            _v0 = materialHeight * (1 - uv0._v);
            _v1 = materialHeight * (1 - uv1._v);
            _v2 = materialHeight * (1 - uv2._v);
      
            // Fix perpendicular projections
            if ((_u0 == _u1 && _v0 == _v1) || (_u0 == _u2 && _v0 == _v2)) {
            	if (_u0 > 0.05)
                	_u0 -= 0.05;
                else
                	_u0 += 0.05;
                	
                if (_v0 > 0.07)           
                	_v0 -= 0.07;
                else
                	_v0 += 0.07;
            }
    
            if (_u2 == _u1 && _v2 == _v1) {
            	if (_u2 > 0.04)
                	_u2 -= 0.04;
                else
                	_u2 += 0.04;
                	
                if (_v2 > 0.06)           
                	_v2 -= 0.06;
                else
                	_v2 += 0.06;
            }
            
        	_invtexmapping.a = _u1 - _u0;
        	_invtexmapping.b = _v1 - _v0;
        	_invtexmapping.c = _u2 - _u0;
        	_invtexmapping.d = _v2 - _v0;
        	
            if (material is BitmapMaterialContainer) {
            	_invtexmapping.tx = _u0 - faceVO.bitmapRect.x;
            	_invtexmapping.ty = _v0 - faceVO.bitmapRect.y;
            } else {
            	_invtexmapping.tx = _u0;
            	_invtexmapping.ty = _v0;
            }
            
            return _invtexmapping;
        }
        
		/**
		 * @inheritDoc
		 */
        public override final function getZ(x:Number, y:Number):Number
        {
            focus = view.camera.focus;
			
			if (_vertexCount > 3)
				return screenZ;
			
            ax = v0x;
            ay = v0y;
            az = v0z;
            bx = v1x;
            by = v1y;
            bz = v1z;
            cx = v2x;
            cy = v2y;
            cz = v2z;

            if ((ax == x) && (ay == y))
                return az;

            if ((bx == x) && (by == y))
                return bz;

            if ((cx == x) && (cy == y))
                return cz;

            azf = az / focus;
            bzf = bz / focus;
            czf = cz / focus;

            faz = 1 + azf;
            fbz = 1 + bzf;
            fcz = 1 + czf;

            axf = ax*faz - x*azf;
            bxf = bx*fbz - x*bzf;
            cxf = cx*fcz - x*czf;
            ayf = ay*faz - y*azf;
            byf = by*fbz - y*bzf;
            cyf = cy*fcz - y*czf;

            det = axf*(byf - cyf) + bxf*(cyf - ayf) + cxf*(ayf - byf);
            da = x*(byf - cyf) + bxf*(cyf - y) + cxf*(y - byf);
            db = axf*(y - cyf) + x*(cyf - ayf) + cxf*(ayf - y);
            dc = axf*(byf - y) + bxf*(y - ayf) + x*(ayf - byf);

            return (da*az + db*bz + dc*cz) / det;
        }
		
		/**
		 * Calulates the uv value of a precise point on the drawing primitive.
		 * Used to determine the mouse position in interactive materials.
		 * 
		 * @param	x	The x position of the point to be tested.
		 * @param	y	The y position of the point to be tested.
		 * @return		The uv value.
		 */
        public function getUV(x:Number, y:Number):UV
        {
            if (uv0 == null)
                return null;

            if (uv1 == null)
                return null;

            if (uv2 == null)
                return null;

            au = uv0._u;
            av = uv0._v;
            bu = uv1._u;
            bv = uv1._v;
            cu = uv2._u;
            cv = uv2._v;

            focus = view.camera.focus;

            ax = v0x;
            ay = v0y;
            az = v0z;
            bx = v1x;
            by = v1y;
            bz = v1z;
            cx = v2x;
            cy = v2y;
            cz = v2z;

            if ((ax == x) && (ay == y))
                return uv0;

            if ((bx == x) && (by == y))
                return uv1;

            if ((cx == x) && (cy == y))
                return uv2;

            azf = az / focus;
            bzf = bz / focus;
            czf = cz / focus;

            faz = 1 + azf;
            fbz = 1 + bzf;
            fcz = 1 + czf;
                                
            axf = ax*faz - x*azf;
            bxf = bx*fbz - x*bzf;
            cxf = cx*fcz - x*czf;
            ayf = ay*faz - y*azf;
            byf = by*fbz - y*bzf;
            cyf = cy*fcz - y*czf;

            det = axf*(byf - cyf) + bxf*(cyf - ayf) + cxf*(ayf - byf);
            da = x*(byf - cyf) + bxf*(cyf - y) + cxf*(y- byf);
            db = axf*(y - cyf) + x*(cyf - ayf) + cxf*(ayf - y);
            dc = axf*(byf - y) + bxf*(y - ayf) + x*(ayf - byf);

            return new UV((da*au + db*bu + dc*cu) / det, (da*av + db*bv + dc*cv) / det);
        }
        
		/**
		 * @inheritDoc
		 */
        public override final function quarter(focus:Number):Array
        {
            if (area > -20 && area < 20)
                return null;
            
			var vertexIndex:int = screenVertices.length/3;
			
			var index0:int = screenIndices.length;
        	screenIndices[screenIndices.length] = screenIndices[startIndex];
        	screenIndices[screenIndices.length] = vertexIndex;
        	screenIndices[screenIndices.length] = vertexIndex+2;
        	var index1:int = screenIndices.length;
        	screenIndices[screenIndices.length] = screenIndices[startIndex+1];
        	screenIndices[screenIndices.length] = vertexIndex+1;
        	screenIndices[screenIndices.length] = vertexIndex;
        	var index2:int = screenIndices.length;
        	screenIndices[screenIndices.length] = screenIndices[startIndex+2];
        	screenIndices[screenIndices.length] = vertexIndex+2;
        	screenIndices[screenIndices.length] = vertexIndex+1;
        	var index3:int = screenIndices.length;
        	screenIndices[screenIndices.length] = vertexIndex;
        	screenIndices[screenIndices.length] = vertexIndex+1;
        	screenIndices[screenIndices.length] = vertexIndex+2;
        	var index4:int = screenIndices.length;
        	
        	ScreenVertex.median(startIndex, startIndex+1, screenVertices, screenIndices, focus);
        	ScreenVertex.median(startIndex+1, startIndex+2, screenVertices, screenIndices, focus);
        	ScreenVertex.median(startIndex+2, startIndex, screenVertices, screenIndices, focus);
        	
            uv01 = UV.median(uv0, uv1);
            uv12 = UV.median(uv1, uv2);
            uv20 = UV.median(uv2, uv0);

            return [
                create(source, faceVO, material, screenVertices, screenIndices, screenCommands, index0, index1, uv0,  uv01, uv20, true),
                create(source, faceVO, material, screenVertices, screenIndices, screenCommands, index1, index2, uv1,  uv12, uv01, true),
                create(source, faceVO, material, screenVertices, screenIndices, screenCommands, index2, index3, uv2,  uv20, uv12, true),
                create(source, faceVO, material, screenVertices, screenIndices, screenCommands, index3, index4, uv01, uv12, uv20, true)
            ];
        }
        
		/**
		 * @inheritDoc
		 */
        public override final function contains(x:Number, y:Number):Boolean
        {
        	//special case for shapes - uses crossing count on an infinite ray projected from the test point along the x axis
        	if (_vertexCount > 3) {
        		var c:Boolean = false;
        		var i:int = startIndex;
        		var j:int = endIndex - 1;
        		var vertix:Number;
        		var vertiy:Number;
        		var vertjx:Number;
        		var vertjy:Number;
        		var iIndex:int;
        		var jIndex:int;
				while (i < endIndex) {
					if (screenCommands[i] == DrawingCommand.CURVE)
						i++;
					
					if ((((vertiy = screenVertices[(iIndex = screenIndices[i]*3)+1]) > y) != ((vertjy = screenVertices[(jIndex = screenIndices[j]*3)+1]) > y)) && (x < ((vertjx = screenVertices[jIndex]) - (vertix = screenVertices[iIndex]))*(y - vertiy)/(vertjy - vertiy) + vertix))
						c = !c;
					
					j = i++;
					
					if (screenCommands[i] == DrawingCommand.MOVE)
						j = i++;
				}
				return c;
        	}
        	
            if ((v0x*(y - v1y) + v1x*(v0y - y) + x*(v1y - v0y))*_areaSign < -0.001)
                return false;

            if ((v0x*(v2y - y) + x*(v0y - v2y) + v2x*(y - v0y))*_areaSign < -0.001)
                return false;

            if ((x*(v2y - v1y) + v1x*(y - v2y) + v2x*(v1y - y))*_areaSign < -0.001)
                return false;

            return true;
        }

        public final function distanceToCenter(x:Number, y:Number):Number
        {
            var centerx:Number = (v0x + v1x + v2x) / 3,
                centery:Number = (v0y + v1y + v2y) / 3;

            return Math.sqrt((centerx-x)*(centerx-x) + (centery-y)*(centery-y));
        }
        
		/**
		 * @inheritDoc
		 */
        public override function calc():void
        {   
        	_index = screenIndices[startIndex]*3;
        	v0x = screenVertices[_index];
        	v0y = screenVertices[_index+1];
        	v0z = screenVertices[_index+2];
        	
        	_index = screenIndices[startIndex+1]*3;
        	v1x = screenVertices[_index];
        	v1y = screenVertices[_index+1];
        	v1z = screenVertices[_index+2];
        	
        	_index = screenIndices[startIndex+2]*3;
        	v2x = screenVertices[_index];
        	v2y = screenVertices[_index+1];
        	v2z = screenVertices[_index+2];
            
            _vertexCount = endIndex - startIndex;
            
            if(_vertexCount > 3) {
            	
            	screenZ = 0;
            	_index = endIndex;
            	minX = Infinity;
            	maxX = -Infinity;
            	minY = Infinity;
            	maxY = -Infinity;
            	minZ = Infinity;
            	maxZ = -Infinity;
            	while (_index-- > startIndex) {
            		_vertex = screenIndices[_index]*3;
	            	//calculate bounding box
	            	_x = screenVertices[_vertex];
	            	_y = screenVertices[_vertex+1];
	            	_z = screenVertices[_vertex+2];
            		if (minX > _x)
            			minX = _x;
            		if (maxX < _x)
            			maxX = _x;
            		if (minY > _y)
            			minY = _y;
            		if (maxY < _y)
            			maxY = _y;
            		if (minZ > _z)
            			minZ = _z;
            		if (maxZ < _z)
            			maxZ = _z;
	            	//calculate screenZ used for sorting
            		screenZ += _z;
            	}
            	
            	screenZ /= _vertexCount;
            	
            } else {
            	//calculate bounding box
            	if (v0x > v1x) {
	                if (v0x > v2x) maxX = v0x;
	                else maxX = v2x;
	            } else {
	                if (v1x > v2x) maxX = v1x;
	                else maxX = v2x;
	            }
	            
	            if (v0x < v1x) {
	                if (v0x < v2x) minX = v0x;
	                else minX = v2x;
	            } else {
	                if (v1x < v2x) minX = v1x;
	                else minX = v2x;
	            }
	            
	            if (v0y > v1y) {
	                if (v0y > v2y) maxY = v0y;
	                else maxY = v2y;
	            } else {
	                if (v1y > v2y) maxY = v1y;
	                else maxY = v2y;
	            }
	            
	            if (v0y < v1y) {
	                if (v0y < v2y) minY = v0y;
	                else minY = v2y;
	            } else {
	                if (v1y < v2y) minY = v1y;
	                else minY = v2y;
	            }
	            
	            if (v0z > v1z) {
	                if (v0z > v2z) maxZ = v0z;
	                else maxZ = v2z;
	            } else {
	                if (v1z > v2z) maxZ = v1z;
	                else maxZ = v2z;
	            }
	            
	            if (v0z < v1z) {
	                if (v0z < v2z) minZ = v0z;
	                else minZ = v2z;
	            } else {
	                if (v1z < v2z) minZ = v1z;
	                else minZ = v2z;
	            }
	            
	            //calculate screenZ used for sorting
            	screenZ = (v0z + v1z + v2z) / 3;
            }
            
            area = 0.5 * (v0x*(v2y - v1y) + v1x*(v0y - v2y) + v2x*(v1y - v0y));
            
            if (area > 0)
        		_areaSign = 1;
        	else
        		_areaSign = -1;
        		
        	// Disabled due to reported errors...
        	/* if(_vertexCount > 3 && reverseArea)
        	{
        		area *= -1;
        		_areaSign *= -1;
        	} */
        }
        
		/**
		 * @inheritDoc
		 */
        public override function toString():String
        {
            var color:String = "";
            if (material is WireColorMaterial)
            {
                switch ((material as WireColorMaterial).color)
                {
                    case 0x00FF00: color = "green"; break;
                    case 0xFFFF00: color = "yellow"; break;
                    case 0xFF0000: color = "red"; break;
                    case 0x0000FF: color = "blue"; break;
                }
            }
            return "T{"+color+int(area)+" screenZ = " + num(screenZ) + ", minZ = " + num(minZ) + ", maxZ = " + num(maxZ) + " }";
        }
    }
}
