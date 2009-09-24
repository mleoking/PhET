package org.papervision3d.core.effects.utils
{
	public class ConvolutionMatrices
	{
		public static var SHARPEN:Array = [0, -1, 0, -1, 20, -1, 0, -1, 0];
		public static var BRIGHTNESS:Array = [5, 5, 5, 5, 0, 5, 5, 5, 5];
		public static var EXTRUDE:Array = [-30, 30, 0,-30, 30, 0,-30, 30, 0];
		public static var EMBOSS:Array = [-2, -1, 0, -1, 1, 1, 0, 1, 2];
		public static var BLUR:Array = [1, 1, 1, 1, 1, 1, 1, 1, 1];

	}
}