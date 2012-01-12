package away3d.core.base
{
    import away3d.arcane;
    import away3d.core.draw.*;
    import away3d.core.math.*;
    import away3d.core.utils.*;

    use namespace arcane;
    
    /**
    * A vertex coordinate value object.
    * Properties x, y and z represent a 3d point in space.
    */
    public class Vertex extends ValueObject
    {
    	/** @private */
        arcane var _index:int;
		/** @private */
        arcane var _x:Number;
        /** @private */
        arcane var _y:Number;
        /** @private */
        arcane var _z:Number;
        /** @private */
        arcane function getVertexDirty():Boolean
        {
        	if (_positionDirty)
        		updatePosition();
        	
        	if (_vertexDirty) {
        		_vertexDirty = false;
        		return true;
        	}
        	
        	return false;
        }
        private var _position:Number3D = new Number3D();
        private var _positionDirty:Boolean;
        private var _vertexDirty:Boolean;
        private var _persp:Number;
        
        private function updatePosition():void
        {
        	_positionDirty = false;
			
			for each (var _element:Element in parents)
				_element.vertexDirty = true;
			
			_vertexDirty = true;
			
        	_position.x = _x;
        	_position.y = _y;
        	_position.z = _z;
        }
        
        public var parents:Array = [];
        
        public var geometry:Geometry;
        
        /**
        * An object that contains user defined properties. Defaults to  null.
        */
        public var extra:Object;
        
    	/**
    	 * Defines the x coordinate of the vertex relative to the local coordinates of the parent mesh object.
    	 */
        public function get x():Number
        {
        	if (_positionDirty)
        		updatePosition();
        	
            return _x;
        }
        
        public function set x(val:Number):void
        {
        	if (_x == val)
        		return;
        	
        	_x = val;
        	
            _positionDirty = true;
        }
        
    	/**
    	 * Represents the y coordinate of the vertex relative to the local coordinates of the parent mesh object.
    	 */
        public function get y():Number
        {
        	if (_positionDirty)
        		updatePosition();
        	
            return _y;
        }
        
        public function set y(val:Number):void
        {
        	if (_y == val)
        		return;
        	
        	_y = val;
        	
            _positionDirty = true;
        }
        
    	/**
    	 * Represents the z coordinate of the vertex relative to the local coordinates of the parent mesh object.
    	 */
        public function get z():Number
        {
        	if (_positionDirty)
        		updatePosition();
        	
            return _z;
        }
        
        public function set z(val:Number):void
        {
        	if (_z == val)
        		return;
        	
        	_z = val;
        	
            _positionDirty = true;
        }
        
        /**
        * Represents the vertex position vector
        */
        public function get position():Number3D
        {
        	if (_positionDirty)
        		updatePosition();
        	
            return _position;
        }
        
		/**
		 * Creates a new <code>Vertex</code> object.
		 *
		 * @param	x	[optional]	The local x position of the vertex. Defaults to 0.
		 * @param	y	[optional]	The local y position of the vertex. Defaults to 0.
		 * @param	z	[optional]	The local z position of the vertex. Defaults to 0.
		 */
        public function Vertex(x:Number = 0, y:Number = 0, z:Number = 0)
        {
            _x = x;
            _y = y;
            _z = z;
            
            _positionDirty = true;
        }
		
		/**
		 * Duplicates the vertex properties to another <code>Vertex</code> object
		 * 
		 * @return	The new vertex instance with duplicated properties applied
		 */
        public function clone():Vertex
        {
            return new Vertex(_x, _y, _z);
        }
        		
		/**
		 * Reset the position of the vertex object by Number3D.
		 */
        public function reset():void
        {
			_x = 0;
			_y = 0;
			_z = 0;
			
			_positionDirty = true;
        }
        
        public function transform(m:MatrixAway3D):void
        {
            setValue(_x * m.sxx + _y * m.sxy + _z * m.sxz + m.tx, _x * m.syx + _y * m.syy + _z * m.syz + m.ty, _x * m.szx + _y * m.szy + _z * m.szz + m.tz);
        }
		
		/**
		 * Adjusts the position of the vertex object by Number3D.
		 *
		 * @param	value	Amount to add in Number3D format.
		 */
        public function add(value:Number3D):void
        {
			_x += value.x;
			_y += value.y;
			_z += value.z;
			
			_positionDirty = true;
        }

		/**
		 * Adjusts the position of the vertex object incrementally.
		 *
		 * @param	x	The x position used for adjustment.
		 * @param	y	The x position used for adjustment.
		 * @param	z	The x position used for adjustment.
		 * @param	k	The fraction by which to adjust the vertex values.
		 */
        public function adjust(x:Number, y:Number, z:Number, k:Number = 1):void
        {
            setValue(_x*(1 - k) + x*k, _y*(1 - k) + y*k, _z*(1 - k) + z*k);
        }
        
        /**
        * Applies perspective distortion
        */
        public function perspective(focus:Number):ScreenVertex
        {
            _persp = 1 / (1 + _z / focus);

            return new ScreenVertex(_x * _persp, _y * _persp, _z);
        }
        
        /**
        * Sets the vertex coordinates
        */
        public function setValue(x:Number, y:Number, z:Number):void
        {
            _x = x;
            _y = y;
            _z = z;
            
			_positionDirty = true;
        }
        
        /**
        * private Returns the middle-point of two vertices
        */
        public static function median(a:Vertex, b:Vertex):Vertex
        {
            return new Vertex((a._x + b._x)/2, (a._y + b._y)/2, (a._z + b._z)/2);
        }

        /**
        * Get the middle-point of two vertices
        */
        public static function distanceSqr(a:Vertex, b:Vertex):Number
        {
            return (a._x + b._x)*(a._x + b._x) + (a._y + b._y)*(a._y + b._y) + (a._z + b._z)*(a._z + b._z);
        }

        /**
        * Get the weighted average of two vertices
        */
        public static function weighted(a:Vertex, b:Vertex, aw:Number, bw:Number):Vertex
        {                
            var d:Number = aw + bw;
            var ak:Number = aw / d;
            var bk:Number = bw / d;
            return new Vertex(a._x*ak+b._x*bk, a._y*ak + b._y*bk, a._z*ak + b._z*bk);
        }
        
		/**
		 * Used to trace the values of a vertex object.
		 * 
		 * @return A string representation of the vertex object.
		 */
        public override function toString(): String
        {
            return "new Vertex("+_x+", "+_y+", "+_z+")";
        }
    }
}
