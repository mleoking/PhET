package org.papervision3d.core.render.sort
{
	
	public class NullSorter implements IRenderSorter
	{
		
		/**
		 * NullSorter();
		 * 
		 * Doesn't do anything to the renderlist, during the sort phase.
		 */
		public function NullSorter()
		{
		}
		
		public function sort(array:Array):void
		{
			//Do absolutely nothing
		}
		
	}
}