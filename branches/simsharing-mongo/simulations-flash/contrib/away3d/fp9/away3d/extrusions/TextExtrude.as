package away3d.extrusions
{
	import away3d.core.base.*;
	import away3d.core.utils.*;
	import away3d.materials.*;

	public class TextExtrude extends Mesh
	{
		private var _mesh:Mesh;
		private var _face:Face;
		private var _subdivisionsZ:Number;
		private var _subdivisionsXY:Number;
		private var _depth:Number;
		private var _effectiveDrawingCommands:Array;
		private var _material:ITriangleMaterial;
		
		public function TextExtrude(mesh:Mesh, init:Object=null)
		{
			super(init);
			
			_mesh = mesh;
			_face = face;
			_subdivisionsZ = ini.getNumber("subdivisionsZ", 1, {min:1, max:100});
			_subdivisionsXY = ini.getNumber("subdivisionsXY", 1, {min:1, max:100});
			_depth = ini.getNumber("depth", 100);
			
			_material = mesh.material as ITriangleMaterial;
			
			for each(var face:Face in mesh.faces)
				generateFaceExtrusion(face);
		}
		
		private function generateFaceExtrusion(face:Face):void
		{
			_face = face;
			
			_effectiveDrawingCommands = _face.drawingCommands;
			for(var i:uint; i<_subdivisionsXY-1; i++)
				splitDrawingCommands();
			
			extrudeDrawingCommands();
		}
		
		private function splitDrawingCommands():void
		{
			var tempArray:Array = _effectiveDrawingCommands.concat();
			for(var i:uint; i<tempArray.length; i++)
			{
				var splitCommands:Array = BezierUtils.splitDrawingCommand(tempArray[i]);
				
				for each(var generatedCommand:DrawingCommand in splitCommands)
					_effectiveDrawingCommands.push(generatedCommand);
			}
		}
		
		private function extrudeDrawingCommands():void
		{
			var i:uint;
			var j:uint;
			var depth:Number;
			var depthOffset:Number;
			var subdivisionUnit:Number = _depth/_subdivisionsZ;
			for(i = 0; i<_face.drawingCommands.length; i++)
			{
				var drawingCommand:DrawingCommand = _effectiveDrawingCommands[i];
				
				if(drawingCommand.type != DrawingCommand.MOVE)
				{
					for(j = 0; j<_subdivisionsZ; j++)
					{
						depth = subdivisionUnit;
						depthOffset = j*_depth/_subdivisionsZ;
						
						generateDrawingCommandExtrusion(drawingCommand, depth, depthOffset);
					}
				}
			}
		}
		
		private function generateDrawingCommandExtrusion(drawingCommand:DrawingCommand, depth:Number, depthOffset:Number):void
		{
			var originalStart:Vertex = drawingCommand.pStart || new Vertex();
			var originalControl:Vertex = drawingCommand.pControl || new Vertex();
			var originalEnd:Vertex = drawingCommand.pEnd;
			
			var pStart:Vertex = new Vertex(originalStart.x, originalStart.y, originalStart.z + depthOffset);
			var pControl:Vertex = new Vertex(originalControl.x, originalControl.y, originalControl.z + depthOffset);
			var pEnd:Vertex = new Vertex(originalEnd.x, originalEnd.y, originalEnd.z + depthOffset);
			
			var pStartOffset:Vertex = new Vertex(pStart.x, pStart.y, pStart.z + depth);
			var pControlOffset:Vertex = new Vertex(pControl.x, pControl.y, pControl.z + depth);
			var pEndOffset:Vertex = new Vertex(pEnd.x, pEnd.y, pEnd.z + depth);
			
			var face:Face = new Face();
			face.material = _material;
		
		 	face.moveTo(_mesh.x + pStart.x, _mesh.y + pStart.y, pStart.z);
			
			face.lineTo(_mesh.x + pStartOffset.x, _mesh.y + pStartOffset.y, pStartOffset.z);
			
			if(drawingCommand.type == DrawingCommand.LINE)
				face.lineTo(_mesh.x + pEndOffset.x, _mesh.y + pEndOffset.y, pEndOffset.z);
			else if(drawingCommand.type == DrawingCommand.CURVE)
				face.curveTo(_mesh.x + pControlOffset.x, _mesh.y + pControlOffset.y, pControlOffset.z, _mesh.x + pEndOffset.x, _mesh.y + pEndOffset.y, pEndOffset.z);
				
			face.lineTo(_mesh.x + pEnd.x, _mesh.y + pEnd.y, pEnd.z);
			
			var pStartAux:Vertex = new Vertex(originalStart.x, originalStart.y, originalStart.z + depthOffset);
			if(drawingCommand.type == DrawingCommand.LINE)
				face.lineTo(_mesh.x + pStartAux.x, _mesh.y + pStartAux.y, pStartAux.z);
			else if(drawingCommand.type == DrawingCommand.CURVE)
				face.curveTo(_mesh.x + pControl.x, _mesh.y + pControl.y, pControl.z, _mesh.x + pStartAux.x, _mesh.y + pStartAux.y, pStartAux.z); 
				
			addFace(face); 
		}
	}
}