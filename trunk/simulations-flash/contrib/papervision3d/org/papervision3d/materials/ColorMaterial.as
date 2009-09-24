package org.papervision3d.materials
{
import flash.display.BitmapData;
import flash.display.Graphics;
import flash.geom.Matrix;

import org.papervision3d.core.material.TriangleMaterial;
import org.papervision3d.core.proto.MaterialObject3D;
import org.papervision3d.core.render.command.RenderTriangle;
import org.papervision3d.core.render.data.RenderSessionData;
import org.papervision3d.core.render.draw.ITriangleDrawer;

/**
* The ColorMaterial class creates a solid color material.
*
* Materials collects data about how objects appear when rendered.
*
*/
public class ColorMaterial extends TriangleMaterial implements ITriangleDrawer
{
	
	/**
	* The ColorMaterial class creates a solid color material.
	*
	* @param	asset				A BitmapData object.
	*/
	public function ColorMaterial( color:Number=0xFF00FF, alpha:Number = 1, interactive:Boolean = false )
	{
		this.fillColor = color;
		this.fillAlpha = alpha;
		this.interactive = interactive;
	}
	
	/**
	 *  drawTriangle
	 */
	
	override public function drawTriangle(tri:RenderTriangle, graphics:Graphics, renderSessionData:RenderSessionData, altBitmap:BitmapData=null, altUV:Matrix=null):void{
		
		var x0:Number = tri.v0.x;
		var y0:Number = tri.v0.y;
		var x1:Number = tri.v1.x;
		var y1:Number = tri.v1.y;
		var x2:Number = tri.v2.x;
		var y2:Number = tri.v2.y;
		
		graphics.beginFill( fillColor, fillAlpha );
		graphics.moveTo( x0, y0 );
		graphics.lineTo( x1, y1 );
		graphics.lineTo( x2, y2 );
		graphics.lineTo( x0, y0 );
		graphics.endFill();
		
		renderSessionData.renderStatistics.triangles++;
	}
	
	/**
	* Returns a string value representing the material properties in the specified ColorMaterial object.
	*
	* @return	A string.
	*/
	public override function toString(): String
	{
		return 'ColorMaterial - color:' + this.fillColor + ' alpha:' + this.fillAlpha;
	}
	
	override public function clone():MaterialObject3D
	{
		var cloned:MaterialObject3D = new ColorMaterial();
		cloned.copy(this);
    	return cloned;
	}


}
}