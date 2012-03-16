package away3d.core.clip
{
    import away3d.core.draw.*;
    import away3d.core.utils.*;

    /**
    * Rectangle clipping
    */
    public class RectangleClipping extends Clipping
    {
        public function RectangleClipping(init:Object = null)
        {
            super(init);
            
            objectCulling = ini.getBoolean("objectCulling", false);
        }
        
		/**
		 * @inheritDoc
		 */
        public override function checkPrimitive(pri:DrawPrimitive):Boolean
        {
            if (pri.maxX < minX)
                return false;
            if (pri.minX > maxX)
                return false;
            if (pri.maxY < minY)
                return false;
            if (pri.minY > maxY)
                return false;
			
            return true;
        }
        
		public override function clone(object:Clipping = null):Clipping
        {
        	var clipping:RectangleClipping = (object as RectangleClipping) || new RectangleClipping();
        	
        	super.clone(clipping);
        	
        	return clipping;
        }
    }
}