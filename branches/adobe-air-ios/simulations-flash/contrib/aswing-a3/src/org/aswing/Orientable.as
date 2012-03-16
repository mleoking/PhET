/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing{

/**
 * An component or any thing has HORIZONTAL or VERTICAL form.
 * @author iiley
 */
public interface Orientable{

    /**
     * Sets the orientation, or how the splitter is divided. The options
     * are:
     * <ul>
     * <li>AsWingConstants.HORIZONTAL</li>
     * <li>AsWingConstants.VERTICAL</li>
     * </ul>
     *
     * @param orientation an integer specifying the orientation
     */
    function setOrientation(ori:int):void;

    /**
     * Returns the orientation.
     * 
     * @return an integer giving the orientation
     * @see #setOrientation()
     */
    function getOrientation():int;
}
}