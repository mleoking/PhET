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
    * Wire material for solid color drawing with optional face border outlining
    */
    public class WireColorMaterial extends EventDispatcher implements ITriangleMaterial
    {
    	/** @private */
        arcane var _id:int;
        
        /**
        * Instance of the Init object used to hold and parse default property values
        * specified by the initialiser object in the 3d object constructor.
        */
		protected var ini:Init;
		
		/**
		 * Determines the color value of the material
		 */
        public var color:int;
        
    	/**
    	 * Determines the alpha value of the material
    	 */
        public var alpha:Number;
        
    	/**
    	 * Determines the wire width
    	 */
        public var width:Number;
        
    	/**
    	 * Determines the color value of the border wire
    	 */
        public var wirecolor:int;
        
    	/**
    	 * Determines the alpha value of the border wire
    	 */
        public var wirealpha:Number;
        
		/**
		 * @inheritDoc
		 */
        public function get visible():Boolean
        {
            return (alpha > 0) || (wirealpha > 0);
        }
        
		/**
		 * @inheritDoc
		 */
        public function get id():int
        {
            return _id;
        }
        
		/**
		 * Creates a new <code>WireColorMaterial</code> object.
		 * 
		 * @param	color				A string, hex value or colorname representing the color of the material.
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
        public function WireColorMaterial(color:* = null, init:Object = null)
        {
            if (color == null)
                color = "random";

            this.color = Cast.trycolor(color);

            ini = Init.parse(init);
            
            alpha = ini.getNumber("alpha", 1, {min:0, max:1});
            wirecolor = ini.getColor("wirecolor", 0x000000);
            width = ini.getNumber("width", 1, {min:0});
            wirealpha = ini.getNumber("wirealpha", 1, {min:0, max:1});
        }
        
		/**
		 * @inheritDoc
		 */
        public function updateMaterial(source:Object3D, view:View3D):void
        {
        	
        }
        
		/**
		 * @inheritDoc
		 */
        public function renderTriangle(tri:DrawTriangle):void
        {
			tri.source.session.renderTriangleLineFill(width, color, alpha, wirecolor, wirealpha, tri.screenVertices, tri.screenCommands, tri.screenIndices, tri.startIndex, tri.endIndex);
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
