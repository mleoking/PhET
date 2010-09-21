package org.papervision3d.core.render.draw
{
	/**
	 * @Author Ralph Hauwert
	 */
	 
	import flash.display.BitmapData;
	import flash.display.Graphics;
	import flash.geom.Matrix;
	
	import org.papervision3d.core.render.command.RenderTriangle;
	import org.papervision3d.core.render.data.RenderSessionData;
	
	public interface ITriangleDrawer
	{
		function drawTriangle(tri:RenderTriangle, graphics:Graphics, renderSessionData:RenderSessionData, altBitmap:BitmapData = null, altUV:Matrix = null):void;
		function drawRT(rt:RenderTriangle, graphics:Graphics, renderSessionData:RenderSessionData):void;
	}
	
	
}