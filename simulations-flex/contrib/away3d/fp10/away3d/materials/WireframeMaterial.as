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
    * Wire material for face border outlining only
    */
    public class WireframeMaterial extends EventDispatcher implements ITriangleMaterial, ISegmentMaterial
    {
    	/** @private */
        arcane var _id:int;
        
        /**
        * Instance of the Init object used to hold and parse default property values
        * specified by the initialiser object in the 3d object constructor.
        */
		protected var ini:Init;
				
		/**
		 * Determines the color value of the wire
		 */
        public var color:int;
		
		/**
		 * Determines the alpha value of the wire
		 */
        public var alpha:Number;
		
		/**
		 * Determines the width value of the wire
		 */
        public var width:Number;
    	
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
		 * Creates a new <code>WireframeMaterial</code> object.
		 * 
		 * @param	color				A string, hex value or colorname representing the color of the wire.
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
        public function WireframeMaterial(color:* = null, init:Object = null)
        {
            if (color == null)
                color = "random";

            this.color = Cast.trycolor(color);

            ini = Init.parse(init);
            
            alpha = ini.getNumber("alpha", 1, {min:0, max:1});
            width = ini.getNumber("width", 1, {min:0});
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
        public function renderSegment(seg:DrawSegment):void
        {
            if (alpha <= 0)
                return;
			
			seg.source.session.renderLine(seg.v0x, seg.v0y, seg.v1x, seg.v1y, width, color, alpha);
        }
        
		/**
		 * @inheritDoc
		 */
        public function renderTriangle(tri:DrawTriangle):void
        {
            if (alpha <= 0)
                return;

            tri.source.session.renderTriangleLine(width, color, alpha, tri.screenVertices, tri.screenCommands, tri.screenIndices, tri.startIndex, tri.endIndex);
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
