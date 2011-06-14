package away3d.core.block
{
	import away3d.core.clip.*;

    /**
    * Array for storing blocker objects
    */
    public class BlockerArray implements IBlockerConsumer
    {
        private var _blockers:Array = [];
        private var _clip:Clipping;
		
		/**
		 * Determines the viewport clipping to be used on blocker primitives.
		 */
		public function get clip():Clipping
		{
			return _clip;
		}
		
		public function set clip(val:Clipping):void
		{
			_clip = val;
			_blockers = [];
		}
        
		/**
		 * @inheritDoc
		 */
        public function blocker(pri:Blocker):void
        {
            if (_clip.checkPrimitive(pri))
            {
                _blockers.push(pri);
            }
        }
		
		/**
		 * Returns a sorted list of blocker primitives for use in <code>BasicRender</code>
		 * 
		 * @see away3d.core.render.BasicRender
		 */
        public function list():Array
        {
            _blockers.sortOn("screenZ", Array.NUMERIC);
            return _blockers;
        }

    }
}
