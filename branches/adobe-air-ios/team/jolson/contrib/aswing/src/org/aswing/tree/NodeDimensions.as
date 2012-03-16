/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.geom.Rectangle;

/**
 * Used by <code>AbstractLayoutCache</code> to determine the size
 * and x origin of a particular node.
 * @author iiley
 */
interface org.aswing.tree.NodeDimensions {
	/**
	 * Returns, by reference in bounds, the size and x origin to
	 * place value at. The calling method is responsible for determining
	 * the Y location. If bounds is <code>null</code>, a newly created
	 * <code>Rectangle</code> should be returned,
	 * otherwise the value should be placed in bounds and returned.
	 *
	 * @param value the <code>value</code> to be represented
	 * @param row row being queried
  	 * @param depth the depth of the row
	 * @param expanded true if row is expanded, false otherwise
	 * @param bounds  a <code>Rectangle</code> containing the size needed
	 *		to represent <code>value</code>
   	 * @return a <code>Rectangle</code> containing the node dimensions,
	 * 		or <code>null</code> if node has no dimension
	 */
	public function countNodeDimensions(value:Object, row:Number, depth:Number, expanded:Boolean, bounds:Rectangle):Rectangle;
}