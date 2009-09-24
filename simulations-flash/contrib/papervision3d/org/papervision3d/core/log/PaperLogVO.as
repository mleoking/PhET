package org.papervision3d.core.log
{
	
	/**
	 * @author Ralph Hauwert
	 */
	public class PaperLogVO
	{
		
		public var level:int;
		public var msg:String;
		public var object:Object;
		public var arg:Array;
		
		public function PaperLogVO(level:int, msg:String, object:Object, arg:Array)
		{
			this.level = level;
			this.msg = msg;
			this.object = object;
			this.arg = arg;	
		}

	}
}