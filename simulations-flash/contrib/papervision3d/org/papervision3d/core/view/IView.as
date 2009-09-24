package org.papervision3d.core.view
{
	/**
	 * @Author Ralph Hauwert
	 */
	public interface IView
	{
		function singleRender():void;
		function startRendering():void;
		function stopRendering(reRender:Boolean = false, cacheAsBitmap:Boolean = false):void;
	}
}