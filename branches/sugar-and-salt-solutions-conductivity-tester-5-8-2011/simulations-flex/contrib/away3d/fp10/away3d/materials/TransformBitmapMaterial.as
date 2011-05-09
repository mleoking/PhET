package away3d.materials
{
    import away3d.arcane;
    import away3d.cameras.lenses.ZoomFocusLens;
    import away3d.containers.*;
    import away3d.core.base.*;
    import away3d.core.draw.*;
    import away3d.core.math.*;
    import away3d.core.utils.*;
    
    import flash.display.*;
    import flash.geom.*;

	use namespace arcane;
	
    /** Basic bitmap texture material */
    public class TransformBitmapMaterial extends BitmapMaterial implements ITriangleMaterial, IUVMaterial
    {
        /** @private */
        arcane var _transform:Matrix = new Matrix();
        
        private var _scaleX:Number = 1;
        private var _scaleY:Number = 1;
        private var _offsetX:Number = 0;
        private var _offsetY:Number = 0;
        private var _rotation:Number = 0;
        private var _projectionVector:Number3D;
        private var _projectionDirty:Boolean;
        private var _N:Number3D = new Number3D();
        private var _M:Number3D = new Number3D();
        private var DOWN:Number3D = new Number3D(0, -1, 0);
        private var RIGHT:Number3D = new Number3D(1, 0, 0);
        private var _transformDirty:Boolean;
        private var _throughProjection:Boolean;
        private var _globalProjection:Boolean;
        private var faceVO:FaceVO;
        private var x:Number;
		private var y:Number;
		private var px:Number;
		private var py:Number;
        private var normalR:Number3D = new Number3D();
        private var _u0:Number;
        private var _u1:Number;
        private var _u2:Number;
        private var _v0:Number;
        private var _v1:Number;
        private var _v2:Number;
        private var _cos:Number;
        private var _sin:Number;
        private var v0x:Number;
        private var v0y:Number;
        private var v0z:Number;
        private var v1x:Number;
        private var v1y:Number;
        private var v1z:Number;
        private var v2x:Number;
        private var v2y:Number;
        private var v2z:Number;
        private var v0:Number3D = new Number3D();
        private var v1:Number3D = new Number3D();
        private var v2:Number3D = new Number3D();
        private var t:Matrix;
		private var _invtexturemapping:Matrix;
		private var fPoint1:Point = new Point();
        private var fPoint2:Point = new Point();
        private var fPoint3:Point = new Point();
        private var mapa:Number;
        private var mapb:Number;
        private var mapc:Number;
        private var mapd:Number;
        private var maptx:Number;
        private var mapty:Number;
        private var mPoint1:Point = new Point();
        private var mPoint2:Point = new Point();
        private var mPoint3:Point = new Point();
        private var mPoint4:Point = new Point();
        private var dot:Number;
		private var line:Point = new Point();
        private var zero:Number;
        private var sign:Number;
		private var point1:Point;
		private var point2:Point;
		private var point3:Point;
		private var flag:Boolean;
		
        private function updateTransform():void
        {
	        _transformDirty = false;
	        
        	//check to see if no transformation exists
        	if (_scaleX == 1 && _scaleY == 1 && _offsetX == 0 && _offsetY == 0 && _rotation == 0) {
        		_transform = null;
        	} else {
	        	_transform = new Matrix();
	        	_transform.scale(_scaleX, _scaleY);
	        	_transform.rotate(_rotation);
	        	_transform.translate(_offsetX, _offsetY);
	        }
	        
	        _materialDirty = true;
        }
        
		private function projectUV(tri:DrawTriangle):Vector.<Number>
        {
        	faceVO = tri.faceVO;
        	
        	if (globalProjection) {
	    		v0.transform(faceVO.v0.position, tri.source.sceneTransform);
	    		v1.transform(faceVO.v1.position, tri.source.sceneTransform);
	    		v2.transform(faceVO.v2.position, tri.source.sceneTransform);
        	} else {
	    		v0 = faceVO.v0.position;
	    		v1 = faceVO.v1.position;
	    		v2 = faceVO.v2.position;
        	}
        	
        	v0x = v0.x;
        	v0y = v0.y;
        	v0z = v0.z;
        	v1x = v1.x;
        	v1y = v1.y;
        	v1z = v1.z;
        	v2x = v2.x;
        	v2y = v2.y;
        	v2z = v2.z;
    		
    		_uvtData[0] = v0x*_N.x + v0y*_N.y + v0z*_N.z;
    		_uvtData[1] = v0x*_M.x + v0y*_M.y + v0z*_M.z;
    		_uvtData[3] = v1x*_N.x + v1y*_N.y + v1z*_N.z;
    		_uvtData[4] = v1x*_M.x + v1y*_M.y + v1z*_M.z;
    		_uvtData[6] = v2x*_N.x + v2y*_N.y + v2z*_N.z;
    		_uvtData[7] = v2x*_M.x + v2y*_M.y + v2z*_M.z;
    		
            return _uvtData;
        }
		
		private function getContainerPoints(rect:Rectangle):Array
		{
			return [rect.topLeft, new Point(rect.top, rect.right), rect.bottomRight, new Point(rect.bottom, rect.left)];
		}
		
		private function getFacePoints(map:Matrix):Array
		{
			fPoint1.x = _u0 = map.tx;
			fPoint2.x = map.a + _u0;
			fPoint3.x = map.c + _u0;
			fPoint1.y = _v0 = map.ty;
			fPoint2.y = map.b + _v0;
			fPoint3.y = map.d + _v0;
			return [fPoint1, fPoint2, fPoint3];
		}
		
        private function getMappingPoints(map:Matrix):Array
        {
        	mapa = map.a*width;
        	mapb = map.b*width;
        	mapc = map.c*height;
        	mapd = map.d*height;
        	maptx = map.tx;
        	mapty = map.ty;
        	mPoint1.x = maptx;
        	mPoint1.y = mapty;
        	mPoint2.x = maptx + mapc;
        	mPoint2.y = mapty + mapd;
        	mPoint3.x = maptx + mapa + mapc;
        	mPoint3.y = mapty + mapb + mapd;
        	mPoint4.x = maptx + mapa;
        	mPoint4.y = mapty + mapb;
        	return [mPoint1, mPoint2, mPoint3, mPoint4]; 
        }
        
		private function findSeparatingAxis(points1:Array, points2:Array):Boolean
		{
			if (checkEdge(points1, points2))
				return true;
			if (checkEdge(points2, points1))
				return true;
			return false;
		}
		
		private function checkEdge(points1:Array, points2:Array):Boolean
		{
            var _length:int = points1.length;
            var i:String;
            for (i in points1) {
            	//get point 1
            	point2 = points1[i];
            	
            	//get point 2
            	if (int(i) == 0) {
            		point1 = points1[_length-1];
            		point3 = points1[_length-2];
            	} else {
            		point1 = points1[int(i)-1];
            		if (int(i) == 1)
            			point3 = points1[_length-1];
            		else
            			point3 = points1[int(i)-2];
            	}
            	
            	//calulate perpendicular line
            	line.x = point2.y - point1.y;
            	line.y = point1.x - point2.x;
            	zero = point1.x*line.x + point1.y*line.y;
            	sign = zero - point3.x*line.x - point3.y*line.y;
            	
            	//calculate each projected value for points2
				flag = true;
				var point:Point;
            	for each (point in points2) {
            		dot = point.x*line.x + point.y*line.y;
            		//return if zero is greater than dot
            		if (zero*sign > dot*sign) {
            			flag = false;
            			break;
            		}
            	}
            	if (flag)
            		return true;
            }
			return false;
		}
		
		/**
		 * @inheritDoc
		 */
		protected override function getMapping(tri:DrawTriangle):Matrix
		{
			if (tri.generated) {
		        //use projectUV if projection vector detected
	    		if (projectionVector) {
	    			//_texturemapping = projectUV(tri);
	    		} else {
	    			_texturemapping = tri.transformUV(this).clone();
	        		_texturemapping.invert();
	    		}
    		
	        	//apply transform matrix if one exists
        		if (_transform) {
        			_mapping = _transform.clone();
	        		_mapping.concat(_texturemapping);
	        	} else {
	        		_mapping = _texturemapping;
	        	}
	        	
	        	return _mapping;
			}
			
        	_faceMaterialVO = getFaceMaterialVO(tri.faceVO, tri.source);
        	
        	//check to see if rendering can be skipped
        	if (!_faceMaterialVO.invalidated)
        		return _faceMaterialVO.texturemapping;
    	
    		_faceMaterialVO.invalidated = false;
    		
    		//use projectUV if projection vector detected
    		if (projectionVector) {
    			//_texturemapping = projectUV(tri);
    		} else {
    			_texturemapping = tri.transformUV(this).clone();
        		_texturemapping.invert();
    		}
    		
    		//apply transform matrix if one exists
    		if (_transform) {
    			_faceMaterialVO.texturemapping = _transform.clone();
        		_faceMaterialVO.texturemapping.concat(_texturemapping);
        		
        		return _faceMaterialVO.texturemapping;
        	}
        	
        	return _faceMaterialVO.texturemapping = _texturemapping;
		}
		
		protected override function getUVData(tri:DrawTriangle):Vector.<Number>
		{
			if (tri.view.camera.lens is ZoomFocusLens)
        		_focus = tri.view.camera.focus;
        	else
        		_focus = 0;
			
			if (tri.generated) {
				_uvtData[2] = 1/(_focus + tri.v0z);
				_uvtData[5] = 1/(_focus + tri.v1z);
				_uvtData[8] = 1/(_focus + tri.v2z);
				
				if (projectionVector) {
		    		_uvtData = projectUV(tri);
		        	_u0 = (_uvtData[0] - _offsetX)/width;
		        	_u1 = (_uvtData[3] - _offsetX)/width;
		        	_u2 = (_uvtData[6] - _offsetX)/width;
		        	_v0 = (_uvtData[1] - _offsetY)/height;
		        	_v1 = (_uvtData[4] - _offsetY)/height;
		        	_v2 = (_uvtData[7] - _offsetY)/height;
		   		} else {
		   			_u0 = tri.uv0.u - _offsetX/width;
		        	_u1 = tri.uv1.u - _offsetX/width;
		        	_u2 = tri.uv2.u - _offsetX/width;
		        	_v0 = 1 - tri.uv0.v - _offsetY/height;
		        	_v1 = 1 - tri.uv1.v - _offsetY/height;
		        	_v2 = 1 - tri.uv2.v - _offsetY/height;
		   		}
	        	
	        	if (_rotation) {
	        		_uvtData[0] = (_u0*_cos - _v0*_sin)/_scaleX;
	        		_uvtData[1] = (_u0*_sin + _v0*_cos)/_scaleY;
	        		_uvtData[3] = (_u1*_cos - _v1*_sin)/_scaleX;
	        		_uvtData[4] = (_u1*_sin + _v1*_cos)/_scaleY;
	        		_uvtData[6] = (_u2*_cos - _v2*_sin)/_scaleX;
	        		_uvtData[7] = (_u2*_sin + _v2*_cos)/_scaleY;
	        	} else {
	        		_uvtData[0] = _u0/_scaleX;
	        		_uvtData[1] = _v0/_scaleY;
	        		_uvtData[3] = _u1/_scaleX;
	        		_uvtData[4] = _v1/_scaleY;
	        		_uvtData[6] = _u2/_scaleX;
	        		_uvtData[7] = _v2/_scaleY;
	        	}
	        	
	    		return _uvtData;
			}
			
			_faceMaterialVO = getFaceMaterialVO(tri.faceVO, tri.source, tri.view);
			
			_faceMaterialVO.uvtData[2] = 1/(_focus + tri.v0z);
			_faceMaterialVO.uvtData[5] = 1/(_focus + tri.v1z);
			_faceMaterialVO.uvtData[8] = 1/(_focus + tri.v2z);
			
			if (!_faceMaterialVO.invalidated)
				return _faceMaterialVO.uvtData;
			
			_faceMaterialVO.invalidated = false;
        	
        	if (projectionVector) {
	    		_uvtData = projectUV(tri);
	        	_u0 = (_uvtData[0] - _offsetX)/width;
	        	_u1 = (_uvtData[3] - _offsetX)/width;
	        	_u2 = (_uvtData[6] - _offsetX)/width;
	        	_v0 = (_uvtData[1] - _offsetY)/height;
	        	_v1 = (_uvtData[4] - _offsetY)/height;
	        	_v2 = (_uvtData[7] - _offsetY)/height;
	   		} else {
	   			_u0 = tri.uv0.u - _offsetX/width;
	        	_u1 = tri.uv1.u - _offsetX/width;
	        	_u2 = tri.uv2.u - _offsetX/width;
	        	_v0 = 1 - tri.uv0.v - _offsetY/height;
	        	_v1 = 1 - tri.uv1.v - _offsetY/height;
	        	_v2 = 1 - tri.uv2.v - _offsetY/height;
	   		}
        	
        	if (_rotation) {
        		_faceMaterialVO.uvtData[0] = (_u0*_cos - _v0*_sin)/_scaleX;
	        	_faceMaterialVO.uvtData[1] = (_u0*_sin + _v0*_cos)/_scaleY;
	        	_faceMaterialVO.uvtData[3] = (_u1*_cos - _v1*_sin)/_scaleX;
	        	_faceMaterialVO.uvtData[4] = (_u1*_sin + _v1*_cos)/_scaleY;
	        	_faceMaterialVO.uvtData[6] = (_u2*_cos - _v2*_sin)/_scaleX;
	        	_faceMaterialVO.uvtData[7] = (_u2*_sin + _v2*_cos)/_scaleY;
        	} else {
        		_faceMaterialVO.uvtData[0] = _u0/_scaleX;
        		_faceMaterialVO.uvtData[1] = _v0/_scaleY;
        		_faceMaterialVO.uvtData[3] = _u1/_scaleX;
        		_faceMaterialVO.uvtData[4] = _v1/_scaleY;
        		_faceMaterialVO.uvtData[6] = _u2/_scaleX;
        		_faceMaterialVO.uvtData[7] = _v2/_scaleY;
        	}
        	 	
			return _faceMaterialVO.uvtData;
		}
		
		/**
		 * Determines whether a projected texture is visble on the faces pointing away from the projection.
		 * 
		 * @see projectionVector
		 */
        public function get throughProjection():Boolean
        {
        	return _throughProjection;
        }
        
        public function set throughProjection(val:Boolean):void
        {
        	_throughProjection = val;
        	_projectionDirty = true;
        }
        
        /**
        * Determines whether a projected texture uses offsetX, offsetY and projectionVector values relative to scene cordinates.
        * 
        * @see projectionVector
        * @see offsetX
        * @see offsetY
        */
        public function get globalProjection():Boolean
        {
        	return _globalProjection;
        }
        
        public function set globalProjection(val:Boolean):void
        {
        	_globalProjection = val;
        	_projectionDirty = true;
        }
        
        /**
        * Transforms the texture in uv-space
        */
        public function get transform():Matrix
        {
        	return _transform;
        }
        
        public function set transform(val:Matrix):void
        {
        	_transform = val;
        	
        	if (_transform) {
	        	
	        	//recalculate rotation
	        	_rotation = Math.atan2(_transform.b, _transform.a);
				_cos = Math.cos(_rotation);
				_sin = Math.sin(_rotation);
				
	        	//recalculate scale
	        	_scaleX = _transform.a/Math.cos(_rotation);
	        	_scaleY = _transform.d/Math.cos(_rotation);
	        	
	        	//recalculate offset
	        	_offsetX = _transform.tx;
	        	_offsetY = _transform.ty;
	        } else {
	        	_scaleX = _scaleY = 1;
	        	_offsetX = _offsetY = _rotation = 0;
	        }
        	
	        //_materialDirty = true;
        }
        
        /**
        * Scales the x coordinates of the texture in uv-space
        */
        public function get scaleX():Number
        {
        	return _scaleX;
        }
        
        public function set scaleX(val:Number):void
        {
        	if (isNaN(val))
                throw new Error("isNaN(scaleX)");
			
            if (val == Infinity)
                Debug.warning("scaleX == Infinity");
			
            if (val == -Infinity)
                Debug.warning("scaleX == -Infinity");
			
            if (val == 0)
                Debug.warning("scaleX == 0");
            
        	_scaleX = val;
        	
        	_transformDirty = true;
        }
        
        /**
        * Scales the y coordinates of the texture in uv-space
        */
        public function get scaleY():Number
        {
        	return _scaleY;
        }
        
        public function set scaleY(val:Number):void
        {
        	if (isNaN(val))
                throw new Error("isNaN(scaleY)");
			
            if (val == Infinity)
                Debug.warning("scaleY == Infinity");
			
            if (val == -Infinity)
                Debug.warning("scaleY == -Infinity");
			
            if (val == 0)
                Debug.warning("scaleY == 0");
            
        	_scaleY = val;
        	
        	_transformDirty = true;
        }
        
        /**
        * Offsets the x coordinates of the texture in uv-space
        */
        public function get offsetX():Number
        {
        	return _offsetX;
        }
        
        public function set offsetX(val:Number):void
        {
        	if (isNaN(val))
                throw new Error("isNaN(offsetX)");
			
            if (val == Infinity)
                Debug.warning("offsetX == Infinity");
			
            if (val == -Infinity)
                Debug.warning("offsetX == -Infinity");
            
        	_offsetX = val;
        	
        	_transformDirty = true;
        }
        
        /**
        * Offsets the y coordinates of the texture in uv-space
        */
        public function get offsetY():Number
        {
        	return _offsetY;
        }
        
        public function set offsetY(val:Number):void
        {
        	if (isNaN(val))
                throw new Error("isNaN(offsetY)");
			
            if (val == Infinity)
                Debug.warning("offsetY == Infinity");
			
            if (val == -Infinity)
                Debug.warning("offsetY == -Infinity");
            
        	_offsetY = val;
        	
        	_transformDirty = true;
        }
        
        /**
        * Rotates the texture in uv-space
        */
        public function get rotation():Number
        {
        	return _rotation;
        }
        
        public function set rotation(val:Number):void
        {
        	if (isNaN(val))
                throw new Error("isNaN(rotation)");
			
            if (val == Infinity)
                Debug.warning("rotation == Infinity");
			
            if (val == -Infinity)
                Debug.warning("rotation == -Infinity");
            
        	_rotation = val;
        	
        	_cos = Math.cos(_rotation);
			_sin = Math.sin(_rotation);
			
        	_transformDirty = true;
        }
        
        /**
        * Projects the texture in object space, ignoring the uv coordinates of the vertex objects.
        * Texture renders normally when set to <code>null</code>.
        */
        public function get projectionVector():Number3D
        {
        	return _projectionVector;
        }
        
        public function set projectionVector(val:Number3D):void
        {
        	_projectionVector = val;
        	if (_projectionVector) {
        		_N.cross(_projectionVector, DOWN);
	            if (!_N.modulo) _N = RIGHT;
	            _M.cross(_N, _projectionVector);
	            _N.cross(_M, _projectionVector);
	            _N.normalize();
	            _M.normalize();
        	}
        	_projectionDirty = true;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function getPixel32(u:Number, v:Number):uint
        {
			if (_transform) {
	        	x = u*_bitmap.width;
				y = (1 - v)*_bitmap.height;
				
				t = _transform.clone();
				t.invert();
				if (repeat) {
					px = (x*t.a + y*t.c + t.tx)%_bitmap.width;
					py = (x*t.b + y*t.d + t.ty)%_bitmap.height;
					if (px < 0)
						px += _bitmap.width;
					if (py < 0)
						py += _bitmap.height;
        			return _bitmap.getPixel32(px, py);
    			} else
        			return _bitmap.getPixel32(x*t.a + y*t.c + t.tx, x*t.b + y*t.d + t.ty);
   			}
        	return super.getPixel32(u, v);
        }
        
		/**
		 * Creates a new <code>TransformBitmapMaterial</code> object.
		 * 
		 * @param	bitmap				The bitmapData object to be used as the material's texture.
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
        public function TransformBitmapMaterial(bitmap:BitmapData, init:Object = null)
        {
            super(bitmap, init);
            
            transform = ini.getObject("transform", Matrix) as Matrix;
            scaleX = ini.getNumber("scaleX", _scaleX);
            scaleY = ini.getNumber("scaleY", _scaleY);
            offsetX = ini.getNumber("offsetX", _offsetX);
            offsetY = ini.getNumber("offsetY", _offsetY);
            rotation = ini.getNumber("rotation", _rotation);
            projectionVector = ini.getObject("projectionVector", Number3D) as Number3D;
            throughProjection = ini.getBoolean("throughProjection", true);
            globalProjection = ini.getBoolean("globalProjection", false);
        }
        
		/**
		 * @inheritDoc
		 */
        public override function updateMaterial(source:Object3D, view:View3D):void
        {
        	_graphics = null;
        	
        	if (_colorTransformDirty)
        		updateColorTransform();
        	
        	if (_bitmapDirty)
        		updateRenderBitmap();
        	
        	if (_projectionDirty || _transformDirty)
        		invalidateFaces();
        	
        	if (_transformDirty)
        		updateTransform();
        	
        	if (_materialDirty || _blendModeDirty)
        		clearFaces();
        	
        	_projectionDirty = false;
        	_blendModeDirty = false;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function renderTriangle(tri:DrawTriangle):void
        {
        	if (_projectionVector && !throughProjection) {
        		
        		if (globalProjection) {
        			normalR.rotate(tri.faceVO.face.normal, tri.source.sceneTransform);
        			if (normalR.dot(_projectionVector) < 0)
        				return;
        		} else if (tri.faceVO.face.normal.dot(_projectionVector) < 0)
        			return;
        	}
        	
			super.renderTriangle(tri);
        }
        
		/**
		 * @inheritDoc
		 */
		public override function renderBitmapLayer(tri:DrawTriangle, containerRect:Rectangle, parentFaceMaterialVO:FaceMaterialVO):FaceMaterialVO
		{	
			//retrieve the transform
			if (_transform)
				_mapping = _transform.clone();
			else
				_mapping = new Matrix();
			
			//if not projected, draw the source bitmap once
			if (!_projectionVector)
				renderSource(tri.source, containerRect, _mapping);
			
			//get the correct faceMaterialVO
			_faceMaterialVO = getFaceMaterialVO(tri.faceVO);
			
			//pass on resize value
			if (parentFaceMaterialVO.resized) {
				parentFaceMaterialVO.resized = false;
				_faceMaterialVO.resized = true;
			}
			
			//pass on invtexturemapping value
			_faceMaterialVO.invtexturemapping = parentFaceMaterialVO.invtexturemapping;
			
			//check to see if rendering can be skipped
			if (parentFaceMaterialVO.updated || _faceMaterialVO.invalidated || _faceMaterialVO.updated) {
				parentFaceMaterialVO.updated = false;
				
				//retrieve the bitmapRect
				_bitmapRect = tri.faceVO.bitmapRect;
				
				//reset booleans
				if (_faceMaterialVO.invalidated)
					_faceMaterialVO.invalidated = false;
				else
					_faceMaterialVO.updated = true;
				
				//store a clone
				_faceMaterialVO.bitmap = parentFaceMaterialVO.bitmap.clone();
				
				//update the transform based on scaling or projection vector
				if (_projectionVector) {
					
					//calulate mapping
					_invtexturemapping = _faceMaterialVO.invtexturemapping;
					//_mapping.concat(projectUV(tri));
					_mapping.concat(_invtexturemapping);
					
					normalR.clone(tri.faceVO.face.normal);
					
					if (_globalProjection)
						normalR.rotate(normalR, tri.source.sceneTransform);
					
					//check to see if the bitmap (non repeating) lies inside the drawtriangle area
					if ((throughProjection || normalR.dot(_projectionVector) >= 0) && (repeat || !findSeparatingAxis(getFacePoints(_invtexturemapping), getMappingPoints(_mapping)))) {
						
						//store a clone
						if (_faceMaterialVO.cleared)
							_faceMaterialVO.bitmap = parentFaceMaterialVO.bitmap.clone();
						
						_faceMaterialVO.cleared = false;
						_faceMaterialVO.updated = true;
						
						//draw into faceBitmap
						_graphics = _s.graphics;
						_graphics.clear();
						_graphics.beginBitmapFill(_bitmap, _mapping, repeat, smooth);
						_graphics.drawRect(0, 0, _bitmapRect.width, _bitmapRect.height);
			            _graphics.endFill();
						_faceMaterialVO.bitmap.draw(_s, null, _colorTransform, _blendMode, _faceMaterialVO.bitmap.rect);
					}
				} else {
					
					//check to see if the bitmap (non repeating) lies inside the containerRect area
					if (repeat && !findSeparatingAxis(getContainerPoints(containerRect), getMappingPoints(_mapping))) {
						_faceMaterialVO.cleared = false;
						_faceMaterialVO.updated = true;
						
						//draw into faceBitmap
						_faceMaterialVO.bitmap.copyPixels(_sourceVO.bitmap, _bitmapRect, _zeroPoint, null, null, true);
					}
				}
			}
			
			return _faceMaterialVO;
		}
    }
}
