package away3d.core.block
{

    /**
    * Interface for containers capable of storing blockers.
    */
    public interface IBlockerConsumer
    {
		/**
		 * Adds blocker primitive to the consumer.
		 * 
		 * @param	block	The blocker primitive to add.
		 */
        function blocker(block:Blocker):void;
    }
}
