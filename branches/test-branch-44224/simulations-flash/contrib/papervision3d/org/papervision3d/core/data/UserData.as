package org.papervision3d.core.data
{
	
	/**
	 * @author Ralph Hauwert
	 */
	
	public class UserData
	{
		
		public var data:*;
		
		/**
		 * UserData();
		 * 
		 * The UserData class abstracts an end-user defined data object.
		 * 
		 * The UserData class itself can be extends for more typed setting of data.
		 */
		public function UserData(data:*=null)
		{
			this.data = data;
		}
		
	}
}