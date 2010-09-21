package away3d.core.base
{
	import away3d.materials.WireColorMaterial;
	
	public class Graphics3D
	{
		private var _geometry:Geometry;
		private var _currentFace:Face;
		private var _currentMaterial:WireColorMaterial;
		private var _zOffset:Number = 0;
		
		public function set geometry(value:Geometry):void
		{
			_geometry = value;
		}
		
		public function lineStyle(thickness:Number = -1, color:int = -1, alpha:Number = -1):void
		{
			
		}
		
		public function beginFill(color:int = -1, alpha:Number = -1):void
		{
			_currentMaterial = new WireColorMaterial();
			_currentMaterial.wirealpha = 0;
			
			if(color != -1)
				_currentMaterial.color = color;
				
			if(alpha != -1)
				_currentMaterial.alpha = alpha;
				
			if(_currentFace)
				_currentFace.material = _currentMaterial;
		}
		
		public function endFill():void
		{
			
		}
		
		public function moveTo(x:Number, y:Number):void
		{
			_currentFace.moveTo(x, -y, _zOffset);
		}
		
		public function lineTo(x:Number, y:Number):void
		{
			_currentFace.lineTo(x, -y, _zOffset);
		}
		
		public function curveTo(cx:Number, cy:Number, ax:Number, ay:Number):void
		{
			_currentFace.curveTo(cx, -cy, _zOffset, ax, -ay, _zOffset);
		}
		
		public function clear():void
		{
			for each(var face:Face in _geometry.faces)
				_geometry.removeFace(face);
		}
		
		public function startNewShape():void
		{
			_currentFace = new Face();
			_currentFace.material = _currentMaterial;
			_geometry.addFace(_currentFace);
		}
	}
}