/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.Component;

/**
 * @author iiley
 */
interface org.aswing.Cell {
	
	/**
	 * Sets the value of this cell.
	 * @param value which should represent on the component of this cell.
	 */
	public function setCellValue(value):Void;
	
	/**
	 * Returns the value of the cell.
	 * @return the value of the cell.
	 */
	public function getCellValue();
				
	/**
	 * Return the represent component of this cell.
	 * <p>
	 * You must keep this component trigger enalbed then it can make JList know 
	 * when it is pressed then set it to be selected.
	 * <br>
	 * If it is a <code>Container</code> and has children, suggest to make its all 
	 * children trigger disabled, then it can get mouse clicks.
	 * @return a component that reprensent this cell.
	 * @see Component#setTriggerEnabled()
	 */
	public function getCellComponent():Component;
}