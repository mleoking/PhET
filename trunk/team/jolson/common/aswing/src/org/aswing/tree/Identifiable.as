/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * A class implemented Identifiable means that every instance of it has a string ID 
 * to indentify the same instance, same instance must has same idCode, and different instance 
 * must has different idCode.
 * <p>
 * @author iiley
 */
interface org.aswing.tree.Identifiable {
	/**
	 * Returns the idCode(A String) of the instance.
	 */
	public function getIdCode():String;
}