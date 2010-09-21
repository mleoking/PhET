package org.papervision3d.core.render.sort
{
	
	/**
	 * @author Ralph Hauwert
	 */
	public class BasicRenderSorter implements IRenderSorter
	{
		
		//Sorts the renderlist by screenDepth.
		public function sort(array:Array):void
		{
			array.sortOn("screenZ", Array.NUMERIC);
		}
		
	}
}