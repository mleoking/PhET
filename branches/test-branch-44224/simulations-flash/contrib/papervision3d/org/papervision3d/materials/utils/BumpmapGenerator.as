package org.papervision3d.materials.utils
{
	import flash.display.BitmapData;
	import flash.filters.ConvolutionFilter;
	import flash.geom.Point;

	public class BumpmapGenerator
	{
		public static function generateBumpmapFrom(bitmapData : BitmapData) : BitmapData
		{
			var tempMap : BitmapData;
			var p : Point = new Point();
			var convolve : ConvolutionFilter = new ConvolutionFilter();
			convolve.matrixX = 3;
			convolve.matrixY = 3;
			convolve.divisor = 1;
			convolve.bias = 127;
			
			var outputData : BitmapData = new BitmapData(bitmapData.width, bitmapData.height, false, 0x000080);
			
			convolve.matrix = new Array(0, 0, 0, -1, 0, 1, 0, 0, 0);
			tempMap = bitmapData.clone();
			tempMap.applyFilter(bitmapData, tempMap.rect, p, convolve);
			outputData.copyChannel(tempMap, tempMap.rect, p, 1, 1);
			convolve.matrix = new Array(0, -1, 0, 0, 0, 0, 0, 1, 0);
			tempMap = bitmapData.clone();
			tempMap.applyFilter(bitmapData, tempMap.rect, p, convolve);
			outputData.copyChannel(tempMap, tempMap.rect, p, 1, 2);
		
			tempMap.dispose();
			return outputData;
		}
	}
}