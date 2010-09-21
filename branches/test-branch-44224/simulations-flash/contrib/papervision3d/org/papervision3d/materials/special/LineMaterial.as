package org.papervision3d.materials.special
{
	import flash.display.Graphics;
	
	import org.papervision3d.core.proto.MaterialObject3D;
	import org.papervision3d.core.render.command.RenderLine;
	import org.papervision3d.core.render.data.RenderSessionData;
	import org.papervision3d.core.render.draw.ILineDrawer;

	public class LineMaterial extends MaterialObject3D implements ILineDrawer
	{
		
		public function LineMaterial(color:Number = 0xFF0000, alpha:Number = 1)
		{
			super();
			this.lineColor = color;
			this.lineAlpha = alpha;
		}
		
		public function drawLine(line:RenderLine, graphics:Graphics, renderSessionData:RenderSessionData):void
		{	
			graphics.lineStyle( line.size, lineColor, lineAlpha );
			graphics.moveTo( line.v0.x, line.v0.y );
			
			if(line.cV){
				graphics.curveTo(line.cV.x, line.cV.y, line.v1.x, line.v1.y);
			}else{
				graphics.lineTo( line.v1.x, line.v1.y );
			}
			
			graphics.moveTo(0,0);
			graphics.lineStyle();
		}
		
	}
}