package org.papervision3d.core.geom
{
	/**
	 * <p>
	 * The Lines object is a DisplayObject3D that is designed to contain and handle the rendering of
	 * Line3D objects. A Line3D is defined by two 3D vertices; one for each end. A line's start and end 
	 * vertices are converted into 2D space and rendered using the Flash drawing API lineTo method. 
	 * 
	 * Line3D can also render curves; add a control vertex using the Line3D.addControlVertex(...) method.
	 * The line's control vertex is then converted into 2D space and rendered using the Flash drawing API
	 * curveTo method.
	 * 
	 * The line's appearance is defined by its LineMaterial. 
	 * 
	 * </p>
	 * 
	 * <p>
	 * Example:
	 * </p>
	 * <pre><code>
	 * 
	 *  //This example creates a Lines3D DisplayObject3D and adds 100 lines into it. 
	 * 
	 *	var numLines : int = 100; 
	 *	
	 *	var lines3D : Lines3D = new Lines3D(); 
	 *	var lineMaterial : LineMaterial = new LineMaterial(0xff0000, 0.8); 
	 *	var lineWeight : Number = 5; 
	 *	
	 *	for(var i : int = 0; i<numLines; i++)
	 *	{
	 *		var startVertex : Vertex3D = new Vertex3D(Math.random()*200, Math.random()*200, Math.random()*200);
	 *		var endVertex 	: Vertex3D = new Vertex3D(Math.random()*200, Math.random()*200, Math.random()*200);
	 *		
	 *		var line : Line3D = new Line3D(lines3D, lineMaterial, lineWeight, startVertex, endVertex); 
	 *		
	 *		lines3D.addLine(line); 
	 *		
	 *	}
	 *	scene.addChild(lines3D); 
	 *		
 	 * </code></pre>
	 * </p>
	 * 
	 * <p>
	 * See also : LineMaterial
	 * </p>
	 * 
	 * @Author Ralph Hauwert
	 * @Author Seb Lee-Delisle
	 * @Author Alan Owen
 * 	 */
	 
	 
	 
	import org.papervision3d.core.geom.renderables.Line3D;
	import org.papervision3d.core.geom.renderables.Vertex3D;
	import org.papervision3d.core.log.PaperLogger;
	import org.papervision3d.core.render.command.RenderLine;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.render.draw.ILineDrawer;
	import org.papervision3d.materials.special.LineMaterial;
	import org.papervision3d.objects.DisplayObject3D;	

	public class Lines3D extends Vertices3D
	{
		
		public var lines:Array;
		private var _material:ILineDrawer;
		
		
		/**
		 * @param material			The default material for this Lines3D. If ommitted then the default
		 * 							LineMaterial3D is used. 
		 * @param name				An identifier for this Lines object. 
		 */
		 		 
		public function Lines3D(material:LineMaterial = null, name:String=null)
		{
			super(null, name);
			
			if(!material) this.material = new LineMaterial();
			else this.material = material;
			
			init();
		}
		
		private function init():void
		{
			this.lines = new Array();
		}
		
		/**
		* Converts 3D vertices into 2D space, to prepare for rendering onto the stage.
		*
		* @param 	parent				The parent DisplayObject3D
		* @param 	renderSessionData	The renderSessionData object for this render cycle. 
		 * 
		*/		
		public override function project( parent :DisplayObject3D, renderSessionData:RenderSessionData ):Number
		{
			// Vertices
			super.project( parent, renderSessionData );
			
			var line3D:Line3D;
			var screenZ:Number;
			var rc:RenderLine;
			
			for each(line3D in lines)
			{
				if(renderSessionData.viewPort.lineCuller.testLine(line3D))
				{
					rc = line3D.renderCommand;
					
					rc.renderer = line3D.material;
					rc.size = line3D.size;
					
					screenZ += rc.screenZ = (line3D.v0.vertex3DInstance.z + line3D.v1.vertex3DInstance.z)/2;
					
					rc.v0 = line3D.v0.vertex3DInstance;
					rc.v1 = line3D.v1.vertex3DInstance;
					
					renderSessionData.renderer.addToRenderList(rc);
				}
			}
			
			return screenZ/(lines.length+1);
		}
		
		
		/**
		 * Adds a Line3D object to this Lines3D container.  
		 * @param line 	The Line3D object to add. 
		 * 
		 */		
		public function addLine(line:Line3D):void
		{
			lines.push(line);
			line.instance = this;
			if(geometry.vertices.indexOf(line.v0) == -1)
			{
				geometry.vertices.push(line.v0);
			}
			
			if(geometry.vertices.indexOf(line.v1) == -1)
			{
				geometry.vertices.push(line.v1);
			}
			
			if(line.cV){
				if(geometry.vertices.indexOf(line.cV) == -1)
				{
					geometry.vertices.push(line.cV);
				}
				
			}
		}
		
		/**
		 * Creates a new line from the parameters passed and adds it.  
		 * @param size		The weight of the line. 
		 * @param x0		The line's start x position. 
		 * @param y0		The line's start y position. 
		 * @param z0		The line's start z position. 
		 * @param x1		The line's end x position. 
		 * @param y1		The line's end y position. 
		 * @param z1		The line's end z position. 
		 * @return 			The line just created. 
		 * 
		 */		
		public function addNewLine(size:Number, x0:Number, y0:Number, z0:Number, x1:Number, y1:Number, z1:Number) : Line3D
		{
			var line:Line3D = new Line3D(this, material as LineMaterial, size, new Vertex3D(x0,y0,z0), new Vertex3D(x1,y1,z1));
			addLine(line);
			
			return line; 
		
		}
		
		/**
		 * This is identical to addNewLine, except it breaks up the line into several shorter line segments
		 * that together make up the full line. This would be useful for improved z-depth sorting. 
		 * 
		 * @param size		The weight of the line. 
		 * @param segments	The number of segments to break up the line into
		 * @param x0		The line's start x position. 
		 * @param y0		The line's start y position. 
		 * @param z0		The line's start z position. 
		 * @param x1		The line's end x position. 
		 * @param y1		The line's end y position. 
		 * @param z1		The line's end z position. 
		 * @return 			An array of the lines just created. 
		 * 
		 */		
		public function addNewSegmentedLine(size:Number, segments:Number, x0:Number, y0:Number, z0:Number, x1:Number, y1:Number, z1:Number) : Array
		{
			//Do line interpolation, and add a bunch of segments for it.
			var xStep:Number = (x1-x0)/segments;
			var yStep:Number = (y1-y0)/segments;
			var zStep:Number = (z1-z0)/segments;
			
			var newLines : Array = new Array; 
			
			var line:Line3D;
			var pv:Vertex3D = new Vertex3D(x0,y0,z0);
			var nv:Vertex3D;
			for(var n:Number = 0; n<=segments; n++){
				nv = new Vertex3D(x0 + xStep*n, y0+yStep*n, z0+zStep*n);
				line = new Line3D(this, material as LineMaterial, size, pv, nv);
				addLine(line);
				newLines.push(line); 
				pv = nv;
			}
		
			return newLines; 
		}
		
		/**
		 * Removes a line. 
		 * @param line 	The line to remove. 
		 * 
		 */		
		public function removeLine(line:Line3D) : void
		{
			var lineindex : int = lines.indexOf(line);
			if(lineindex>-1) 
			{
				lines.splice(lineindex,1);	
			}
			else
			{
				PaperLogger.warning("Papervision3D Lines3D.removeLine : WARNING removal of non-existant line attempted. ");
			}
		}
		/**
		 * Removes all the lines.  
		 * 
		 */		
		public function removeAllLines():void
		{
			while ( lines.length > 0 )
			{
				removeLine( lines[0] ) ;
			} 		
			
		}
		
		
	}
}