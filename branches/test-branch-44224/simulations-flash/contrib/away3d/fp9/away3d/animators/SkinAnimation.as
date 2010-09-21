package away3d.animators
{
	import away3d.animators.skin.*;
	import away3d.containers.ObjectContainer3D;
	import away3d.core.utils.*;
	
	public class SkinAnimation implements IMeshAnimation
    {
        private var _channels:Array;
        
    	/**
    	 * Defines wether the animation will loop
    	 */
		public var loop:Boolean;
		
		/**
		 * Defines the total length of the animation in seconds
		 */
        public var length:Number;
		
		/**
		 * Defines the start of the animation in seconds
		 */
        public var start:Number;
		
        public function SkinAnimation()
        {
            Debug.trace(" + SkinAnimation");
			_channels = [];
            loop = true;
            length = 0;
        }
		
		/**
		 * Updates all channels in the animation with the given time in seconds.
		 * 
		 * @param	time						Defines the time in seconds of the playhead of the animation.
		 * @param	interpolate		[optional]	Defines whether the animation interpolates between channel points Defaults to true.
		 */
        public function update(time:Number, interpolate:Boolean = true):void
        {
			if (time > start + length ) {
                if (loop) {
                    time = start + (time - start) % length;
                }else{
                    time = start + length;
                }
            } else if (time < start) {
                if (loop) {
                    time = start + (time - start) % length + length;
                }else{
                    time = start;
                }
        	}
        	
            for each (var channel:Channel in _channels)
                channel.update(time, interpolate);
        }
		
		public function clone(object:ObjectContainer3D):IMeshAnimation
		{
			var skinAnimation:SkinAnimation = new SkinAnimation();
			
			skinAnimation.loop = loop;
			skinAnimation.length = length;
			skinAnimation.start = start;
			
			for each (var channel:Channel in _channels)
				skinAnimation.appendChannel(channel.clone(object));
				
			return skinAnimation;
		}
		
		/**
		 * Adds an animation channel to the animation timeline.
		 */
        public function appendChannel(channel:Channel) : void
        {
			_channels.push(channel);
        }
    }
}
