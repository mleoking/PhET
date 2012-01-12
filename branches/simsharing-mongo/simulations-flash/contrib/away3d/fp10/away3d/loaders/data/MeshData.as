package away3d.loaders.data
{
	/**
	 * Data class for the mesh data of a 3d object
	 */
	public class MeshData extends ObjectData
	{
		/**
		 * Defines the geometry used by the mesh instance
		 */
		 public var geometry:GeometryData;
		 
		/**
		 *
		 */
		 public var skeleton:String;
	}
}