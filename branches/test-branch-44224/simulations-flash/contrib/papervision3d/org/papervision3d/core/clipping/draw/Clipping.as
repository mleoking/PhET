package org.papervision3d.core.clipping.draw
{
    
    import flash.display.*;
    import flash.geom.*;
    
    import org.papervision3d.core.render.command.RenderableListItem;

    /**
    * Base clipping class for no clipping.
    */
    public class Clipping
    {
    	private var rectangleClipping:RectangleClipping;
    	private var zeroPoint:Point = new Point(0, 0);
		private var globalPoint:Point;
		
    	/**
    	 * Minimum allowed x value for primitives
    	 */
    	public var minX:Number = -1000000;
    	
    	/**
    	 * Minimum allowed y value for primitives
    	 */
        public var minY:Number = -1000000;
    	
    	/**
    	 * Maximum allowed x value for primitives
    	 */
        public var maxX:Number = 1000000;
    	
    	/**
    	 * Maximum allowed y value for primitives
    	 */
        public var maxY:Number = 1000000;
		
		/**
		 * Checks a drawing primitive for clipping.
		 * 
		 * @param	pri	The drawing primitive being checked.
		 * @return		The clipping result - false for clipped, true for non-clipped.
		 */
        public function check(pri:RenderableListItem):Boolean
        {
            return true;
        }
		
		/**
		 * Checks a bounding rectangle for clipping.
		 * 
		 * @param	minX	The x value for the left side of the rectangle.
		 * @param	minY	The y value for the top side of the rectangle.
		 * @param	maxX	The x value for the right side of the rectangle.
		 * @param	maxY	The y value for the bottom side of the rectangle.
		 * @return		The clipping result - false for clipped, true for non-clipped.
		 */
        public function rect(minX:Number, minY:Number, maxX:Number, maxY:Number):Boolean
        {
            return true;
        }
		
		/**
		 * Returns a rectangle clipping object representing the bounding box of the clipping object.
		 */
        public function asRectangleClipping():RectangleClipping
        {
        	if (!rectangleClipping)
        		rectangleClipping = new RectangleClipping();
        	
        	rectangleClipping.minX = -1000000;
        	rectangleClipping.minY = -1000000;
        	rectangleClipping.maxX = 1000000;
        	rectangleClipping.maxY = 1000000;
        	
            return rectangleClipping;
        }

		/**
		 * Returns a rectangle clipping object initilised with the edges of the flash movie as the clipping bounds.
		 */
        public function screen(container:Sprite):Clipping
        {
        	if (!rectangleClipping)
    			rectangleClipping = new RectangleClipping();
        	
        	switch(container.stage.align)
        	{
        		case StageAlign.TOP_LEFT:
	            	zeroPoint.x = 0;
	            	zeroPoint.y = 0;
	                globalPoint = container.globalToLocal(zeroPoint);
	                
	                rectangleClipping.maxX = (rectangleClipping.minX = globalPoint.x) + container.stage.stageWidth;
	                rectangleClipping.maxY = (rectangleClipping.minY = globalPoint.y) + container.stage.stageHeight;
	                break;
	            case StageAlign.TOP_RIGHT:
	            	zeroPoint.x = container.stage.stageWidth;
	            	zeroPoint.y = 0;
	                globalPoint = container.globalToLocal(zeroPoint);
	                
	                rectangleClipping.minX = (rectangleClipping.maxX = globalPoint.x) - container.stage.stageWidth;
	                rectangleClipping.maxY = (rectangleClipping.minY = globalPoint.y) + container.stage.stageHeight;
	                break;
	            case StageAlign.BOTTOM_LEFT:
	            	zeroPoint.x = 0;
	            	zeroPoint.y = container.stage.stageHeight;
	                globalPoint = container.globalToLocal(zeroPoint);
	                
	                rectangleClipping.maxX = (rectangleClipping.minX = globalPoint.x) + container.stage.stageWidth;
	                rectangleClipping.minY = (rectangleClipping.maxY = globalPoint.y) - container.stage.stageHeight;
	                break;
	            case StageAlign.BOTTOM_RIGHT:
	            	zeroPoint.x = container.stage.stageWidth;
	            	zeroPoint.y = container.stage.stageHeight;
	                globalPoint = container.globalToLocal(zeroPoint);
	                
	                rectangleClipping.minX = (rectangleClipping.maxX = globalPoint.x) - container.stage.stageWidth;
	                rectangleClipping.minY = (rectangleClipping.maxY = globalPoint.y) - container.stage.stageHeight;
	                break;
	            case StageAlign.TOP:
	            	zeroPoint.x = container.stage.stageWidth/2;
	            	zeroPoint.y = 0;
	                globalPoint = container.globalToLocal(zeroPoint);
	                
	                rectangleClipping.minX = globalPoint.x - container.stage.stageWidth/2;
	                rectangleClipping.maxX = globalPoint.x + container.stage.stageWidth/2;
	                rectangleClipping.maxY = (rectangleClipping.minY = globalPoint.y) + container.stage.stageHeight;
	                break;
	            case StageAlign.BOTTOM:
	            	zeroPoint.x = container.stage.stageWidth/2;
	            	zeroPoint.y = container.stage.stageHeight;
	                globalPoint = container.globalToLocal(zeroPoint);
	                
	                rectangleClipping.minX = globalPoint.x - container.stage.stageWidth/2;
	                rectangleClipping.maxX = globalPoint.x + container.stage.stageWidth/2;
	                rectangleClipping.minY = (rectangleClipping.maxY = globalPoint.y) - container.stage.stageHeight;
	                break;
	            case StageAlign.LEFT:
	            	zeroPoint.x = 0;
	            	zeroPoint.y = container.stage.stageHeight/2;
	                globalPoint = container.globalToLocal(zeroPoint);
	                
	                rectangleClipping.maxX = (rectangleClipping.minX = globalPoint.x) + container.stage.stageWidth;
	                rectangleClipping.minY = globalPoint.y - container.stage.stageHeight/2;
	                rectangleClipping.maxY = globalPoint.y + container.stage.stageHeight/2;
	                break;
	            case StageAlign.RIGHT:
	            	zeroPoint.x = container.stage.stageWidth;
	            	zeroPoint.y = container.stage.stageHeight/2;
	                globalPoint = container.globalToLocal(zeroPoint);
	                
	                rectangleClipping.minX = (rectangleClipping.maxX = globalPoint.x) - container.stage.stageWidth;
	                rectangleClipping.minY = globalPoint.y - container.stage.stageHeight/2;
	                rectangleClipping.maxY = globalPoint.y + container.stage.stageHeight/2;
	                break;
	            default:
	            	zeroPoint.x = container.stage.stageWidth/2;
	            	zeroPoint.y = container.stage.stageHeight/2;
	                globalPoint = container.globalToLocal(zeroPoint);
	            	
	                rectangleClipping.minX = globalPoint.x - container.stage.stageWidth/2;
	                rectangleClipping.maxX = globalPoint.x + container.stage.stageWidth/2;
	                rectangleClipping.minY = globalPoint.y - container.stage.stageHeight/2;
	                rectangleClipping.maxY = globalPoint.y + container.stage.stageHeight/2;
        	}
            
            return rectangleClipping;
        }
    }
}