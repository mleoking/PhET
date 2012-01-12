package away3d.materials
{
    import away3d.arcane;
    import away3d.containers.*;
    import away3d.core.base.*;
    import away3d.core.draw.*;
    import away3d.core.render.*;
    import away3d.core.utils.*;
    import away3d.events.*;
    
    import flash.events.*;
	
	use namespace arcane;
	
    /**
    * Material for solid color drawing
    */
    public class ColorMaterial extends EventDispatcher implements ITriangleMaterial, IFogMaterial, IBillboardMaterial
    {
    	/** @private */
        arcane var _id:int;
		/** @private */
        arcane function notifyMaterialUpdate():void
        {
            if (!hasEventListener(MaterialEvent.MATERIAL_UPDATED))
                return;
			
            if (_materialupdated == null)
                _materialupdated = new MaterialEvent(MaterialEvent.MATERIAL_UPDATED, this);
                
            dispatchEvent(_materialupdated);
        }
        
    	private var _color:uint;
    	private var _alpha:Number;
    	private var _materialDirty:Boolean;
    	private var _materialupdated:MaterialEvent;
    	
        /**
        * Instance of the Init object used to hold and parse default property values
        * specified by the initialiser object in the 3d object constructor.
        */
		protected var ini:Init;
		
		/**
		 * 24 bit color value representing the material color
		 */
        public function set color(val:uint):void
        {
        	if (_color == val)
        		return;
        	
        	_color = val;
        	
        	_materialDirty = true;
        }
        
        public function get color():uint
        {
        	return _color;
        }
        
		/**
		 * @inheritDoc
		 */
        public function set alpha(val:Number):void
        {
        	if (_alpha == val)
        		return;
        	
        	_alpha = val;
        	
        	_materialDirty = true;
        }
        
        public function get alpha():Number
        {
        	return _alpha;
        }
        
		/**
		 * @inheritDoc
		 */
        public function get visible():Boolean
        {
            return (alpha > 0);
        }
                
		/**
		 * @inheritDoc
		 */
        public function get id():int
        {
            return _id;
        }
    	
		/**
		 * Creates a new <code>ColorMaterial</code> object.
		 * 
		 * @param	color				A string, hex value or colorname representing the color of the material.
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
        public function ColorMaterial(color:* = null, init:Object = null)
        {
            if (color == null)
                color = "random";

            this.color = Cast.trycolor(color);

            ini = Init.parse(init);
            
            _alpha = ini.getNumber("alpha", 1, {min:0, max:1});
        }
        
		/**
		 * @inheritDoc
		 */
        public function updateMaterial(source:Object3D, view:View3D):void
        {
        	if (_materialDirty) {
        		_materialDirty = false;
        		notifyMaterialUpdate();
        	}
        }
        
		/**
		 * @inheritDoc
		 */
        public function renderTriangle(tri:DrawTriangle):void
        {
        	tri.source.session.renderTriangleColor(_color, _alpha, tri.screenVertices, tri.screenCommands, tri.screenIndices, tri.startIndex, tri.endIndex);
        }
        
		/**
		 * @inheritDoc
		 */
        public function renderBillboard(bill:DrawBillboard):void
        {
            bill.source.session.renderBillboardColor(_color, _alpha, bill);
        }
        
		/**
		 * @inheritDoc
		 */
        public function renderFog(fog:DrawFog):void
        {
            fog.source.session.renderFogColor(fog.clip, _color, _alpha);
        }
        
		/**
		 * @inheritDoc
		 */
        public function clone():IFogMaterial
        {
        	return new ColorMaterial(_color, {alpha:_alpha});
        }
        
		/**
		 * @inheritDoc
		 */
        public function addOnMaterialUpdate(listener:Function):void
        {
        	addEventListener(MaterialEvent.MATERIAL_UPDATED, listener, false, 0, true);
        }
        
		/**
		 * @inheritDoc
		 */
        public function removeOnMaterialUpdate(listener:Function):void
        {
        	removeEventListener(MaterialEvent.MATERIAL_UPDATED, listener, false);
        }
    }
}
