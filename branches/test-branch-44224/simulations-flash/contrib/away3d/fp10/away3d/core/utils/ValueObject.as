package away3d.core.utils
{
    
    import flash.events.EventDispatcher;
    import flash.events.Event;
    
    public class ValueObject extends EventDispatcher
    {
        public function addOnChange(listener:Function):void
        {
            addEventListener("changed", listener, false, 0, true);
        }

        public function removeOnChange(listener:Function):void
        {
            removeEventListener("changed", listener, false);
        }

        private static var changed:Event;

        protected function notifyChange():void
        {
            if (!hasEventListener("changed"))
                return;

            if (changed == null)
                changed = new Event("changed");
                
            dispatchEvent(changed);
        }
    }
}