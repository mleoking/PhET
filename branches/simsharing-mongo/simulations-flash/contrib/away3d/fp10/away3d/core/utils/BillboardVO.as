package away3d.core.utils
{
	import away3d.core.base.*;
	import away3d.materials.*;
	
	public class BillboardVO
	{
		public var vertex:Vertex;
		
		public var command:String;
		
		public var material:IBillboardMaterial;
		
		public var width:Number;
		
		public var height:Number;
		
		public var rotation:Number;
		
		public var scaling:Number;
		
		public var billboard:Billboard;
	}
}