package away3d.cameras.lenses
{
	import away3d.cameras.*;
	import away3d.containers.*;
	import away3d.core.base.Vertex;
	import away3d.core.clip.*;
	import away3d.core.draw.*;
	import away3d.core.geom.*;
	import away3d.core.math.*;
	import away3d.core.utils.*;	

	/**
    * Abstract lens for resolving perspective using the <code>Camera3D</code> object's position and properties
    */
    public class AbstractLens
    {
    	protected const toRADIANS:Number = Math.PI/180;
		protected const toDEGREES:Number = 180/Math.PI;
		
		protected var _view:View3D;
		protected var _drawPrimitiveStore:DrawPrimitiveStore;
		protected var _cameraVarsStore:CameraVarsStore;
		protected var _camera:Camera3D;
		protected var _clipping:Clipping;
		protected var _clipTop:Number;
        protected var _clipBottom:Number;
        protected var _clipLeft:Number;
        protected var _clipRight:Number;
        protected var _clipHeight:Number;
        protected var _clipWidth:Number;
        protected var _focusOverZoom:Number;
        protected var _zoom2:Number;
        protected var _frustum:Frustum;
        protected var _near:Number;
        protected var _far:Number;
        protected var _plane:Plane3D;
        protected var _len:Number;
        
		protected var _vertex:Vertex;
    	protected var _sx:Number;
    	protected var _sy:Number;
    	protected var _sz:Number;
        protected var _sw:Number;
        protected var _vx:Number;
        protected var _vy:Number;
        protected var _vz:Number;
        protected var _scz:Number;
        
        protected var _persp:Number;
        
    	protected var classification:int;
    	protected var viewTransform:MatrixAway3D;
    	protected var view:MatrixAway3D = new MatrixAway3D();
		
		public function get near():Number
		{
			return _near;
		}
		
		public function get far():Number
		{
			return _far;
		}
		
		public function setView(val:View3D):void
		{
			_view = val;
			_drawPrimitiveStore = val.drawPrimitiveStore;
			_cameraVarsStore = val.cameraVarsStore;
			_camera = val.camera;
			_clipping = val.screenClipping;
			_clipTop = _clipping.maxY;
        	_clipBottom = _clipping.minY;
        	_clipLeft = _clipping.minX;
        	_clipRight = _clipping.maxX;
        	_clipHeight = _clipBottom - _clipTop;
        	_clipWidth = _clipRight - _clipLeft;
        	
        	_far = _clipping.maxZ;
		}
    }
}
