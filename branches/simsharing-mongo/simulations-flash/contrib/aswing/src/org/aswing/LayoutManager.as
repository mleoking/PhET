/*
 Copyright aswing.org, see the LICENCE.txt.
*/


import org.aswing.Component;
import org.aswing.Container;
import org.aswing.geom.Dimension;

/** 
 * Defines an interface for classes that know how to layout Containers
 * based on a layout constraints object.
 *
 * @see Component
 * @see Container
 *
 * @version	04/26/05
 * @author 	iiley
 */
interface org.aswing.LayoutManager{
    /**
     * Adds the specified component to the layout, using the specified
     * constraint object.
     * @param comp the component to be added
     * @param constraints  where/how the component is added to the layout.
     */
    public function addLayoutComponent(comp:Component, constraints:Object):Void;

    /**
     * Removes the specified component from the layout.
     * @param comp the component to be removed
     */
    public function removeLayoutComponent(comp:Component):Void;

    /**
     * Calculates the preferred size dimensions for the specified 
     * container, given the components it contains.
     * @param target the container to be laid out
     *  
     * @see #minimumLayoutSize
     */
    public function preferredLayoutSize(target:Container):Dimension;

    /** 
     * Calculates the minimum size dimensions for the specified 
     * container, given the components it contains.
     * @param target the component to be laid out
     * @see #preferredLayoutSize
     */
    public function minimumLayoutSize(target:Container):Dimension;

    /** 
     * Calculates the maximum size dimensions for the specified container,
     * given the components it contains.
     * @param target the component to be laid out
     * @see #preferredLayoutSize
     */
    public function maximumLayoutSize(target:Container):Dimension;
    
    /** 
     * Lays out the specified container.
     * @param target the container to be laid out 
     */
    public function layoutContainer(target:Container):Void;

    /**
     * Returns the alignment along the x axis.  This specifies how
     * the component would like to be aligned relative to other 
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public function getLayoutAlignmentX(target:Container):Number;

    /**
     * Returns the alignment along the y axis.  This specifies how
     * the component would like to be aligned relative to other 
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public function getLayoutAlignmentY(target:Container):Number;

    /**
     * Invalidates the layout, indicating that if the layout manager
     * has cached information it should be discarded.
     */
    public function invalidateLayout(target:Container):Void;
    	
}
