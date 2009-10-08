package away3d.core.draw
{
    import away3d.core.render.*;
    
    import flash.display.DisplayObject;
    import flash.geom.Rectangle;
	
	/**
	 * Displayobject container drawing primitive.
	 */
    public class DrawDisplayObject extends DrawPrimitive
    {
    	private var displayRect:Rectangle;
    	
    	/**
    	 * The x value of the screenvertex used to position the drawing primitive in the view.
    	 */
        public var vx:Number;
		
    	/**
    	 * The y value of the screenvertex used to position the drawing primitive in the view.
    	 */
        public var vy:Number;
        
    	/**
    	 * The z value of the screenvertex used to position the drawing primitive in the view.
    	 */
        public var vz:Number;
        
    	/**
    	 * A reference to the displayobject used by the drawing primitive.
    	 */
        public var displayobject:DisplayObject;
        
    	/**
    	 * A reference to the session used by the drawing primitive.
    	 */
        public var session:AbstractRenderSession;
        
		/**
		 * @inheritDoc
		 */
        public override function calc():void
        {
        	displayRect = displayobject.getBounds(displayobject);
            screenZ = vz;
            minZ = screenZ;
            maxZ = screenZ;
            minX = vx + displayRect.left;
            minY = vy + displayRect.top;
            maxX = vx + displayRect.right;
            maxY = vy + displayRect.bottom;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function clear():void
        {
            displayobject = null;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function render():void
        {
            displayobject.x = vx;// - displayobject.width/2;
            displayobject.y = vy;// - displayobject.height/2;
            session.addDisplayObject(displayobject);
        }
		
		//TODO: correct function for contains in DrawDisplayObject
		/**
		 * @inheritDoc
		 */
        public override function contains(x:Number, y:Number):Boolean
        {
            x;y;//TODO : FDT Warning
            return true;
        }
    }
}
