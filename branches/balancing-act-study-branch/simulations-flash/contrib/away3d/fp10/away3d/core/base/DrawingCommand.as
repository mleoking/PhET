package away3d.core.base
{
	import away3d.core.math.Number3D;
	
	public class DrawingCommand
	{
		public static const MOVE:String = "M";
		public static const LINE:String = "L";
		public static const CURVE:String = "C";
		
		public var pStart:Vertex;
		public var pControl:Vertex;
		public var pEnd:Vertex;
		public var type:String;
		
		public function DrawingCommand(type:String, pStart:Vertex, pControl:Vertex, pEnd:Vertex)
		{
			this.type = type;
			this.pStart = pStart;
			this.pControl = pControl;
			this.pEnd = pEnd;
		}
		
		public function toString():String
		{
			return "DrawingCommand: " + type + ", " + pStart + ", " + pControl + ", " + pEnd;
		}
	}
}