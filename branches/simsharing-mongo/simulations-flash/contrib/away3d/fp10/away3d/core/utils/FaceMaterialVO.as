package away3d.core.utils
{
	import away3d.containers.*;
	import away3d.core.base.*;
	
	import flash.display.BitmapData;
	import flash.geom.Matrix;
	
	public class FaceMaterialVO
	{
		public var source:Object3D;
		public var view:View3D;
		public var invtexturemapping:Matrix;
		public var texturemapping:Matrix;
		public var uvtData:Vector.<Number> = new Vector.<Number>(9);
		
		public var width:int;
		public var height:int;
		public var color:uint;
		
		public var bitmap:BitmapData;
		
		public var cleared:Boolean = true;
		public var updated:Boolean = false;
		public var invalidated:Boolean = true;
		public var backface:Boolean = false;
		public var resized:Boolean;
		
		public function FaceMaterialVO(source:Object3D = null, view:View3D = null)
		{
			this.source = source;
			this.view = view;
		}
		
		public function clear():void
		{
	        cleared = true;
	        updated = true;
		}
		
		public function resize(width:Number, height:Number, transparent:Boolean = true):void
		{
			if (this.width == width && this.height == height)
				return;
			
			resized = true;
			updated = true;
			
			this.width = width;
			this.height = height;
			this.color = color;
			
			if (bitmap)
				bitmap.dispose();
			
			bitmap = new BitmapData(width, height, transparent, 0);
			bitmap.lock();
		}
	}
}