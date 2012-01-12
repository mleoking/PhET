package away3d.core.block
{
    import away3d.core.draw.*;

    /**
    * Abstract primitive that can block other primitives from drawing
    */
    public class Blocker extends DrawPrimitive
    {   
        /**
        * Return value signifies whether the given drawprimitive should be blocked. Called from the PrimitiveArray object on each blocker in the blockers array.
        * 
        * @see away3d.core.draw.PrimitiveArray
        */
        public function block(pri:DrawPrimitive):Boolean
        {
            pri;//TODO : FDT Warning
            return false;
        }

    }
}
