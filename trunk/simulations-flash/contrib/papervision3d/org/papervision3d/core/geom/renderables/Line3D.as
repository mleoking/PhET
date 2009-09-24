package org.papervision3d.core.geom.renderables
{
	
	
	 
	import org.papervision3d.core.geom.Lines3D;
	import org.papervision3d.core.render.command.IRenderListItem;
	import org.papervision3d.core.render.command.RenderLine;
	import org.papervision3d.materials.special.LineMaterial;

	/**
	 * 
	 * Line3D is used by Lines3D to store and render the lines. See Lines3D for a full explanation. 
	 * 
	 * @Author Ralph Hauwert
	 * @Author Andy Zupko
	 * @Author Seb Lee-Delisle
  	 */

	public class Line3D extends AbstractRenderable implements IRenderable
	{
		
		public var v0:Vertex3D;
		public var v1:Vertex3D;
		public var cV:Vertex3D;		
		public var material:LineMaterial;
		public var renderCommand:RenderLine;
		public var size:Number;
		/**
		 * 
		 * @param instance		The containing Lines3D object
		 * @param material		The material for the line
		 * @param size			The line weight
		 * @param vertex0		The start vertex
		 * @param vertex1		The end vertex
		 * 
		 */		
		public function Line3D(instance:Lines3D, material:LineMaterial, size:Number, vertex0:Vertex3D, vertex1:Vertex3D)
		{
			this.size = size;
			this.material = material;
			this.v0 = vertex0;
			this.v1 = vertex1;
			this.cV = vertex1;
			this.instance = instance;
			this.renderCommand = new RenderLine(this);
		}
		
		public function addControlVertex(cx:Number, cy:Number, cz:Number) :void
		{
			cV = new Vertex3D(cx,cy,cz);
			
			if(instance.geometry.vertices.indexOf(cV) == -1){
				instance.geometry.vertices.push(cV);
			}		
			renderCommand.cV = cV.vertex3DInstance; 
		}
		
		
		override public function getRenderListItem():IRenderListItem
		{
			return this.renderCommand;
		}
		
	}
}