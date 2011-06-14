package away3d.loaders.data
{
	/**
	 * Data class for teh material of a 3d object
	 */
	public class MeshMaterialData
	{
		/**
		 * The name of the material used as a unique reference for the mesh.
		 */
		public var symbol:String;
		
		/**
		* A list of faces which are to be drawn with the material.
		*/		
		public var faceList:Array = [];
	}
}