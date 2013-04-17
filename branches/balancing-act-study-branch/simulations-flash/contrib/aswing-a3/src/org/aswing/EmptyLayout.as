/*
 Copyright aswing.org, see the LICENCE.txt.
*/
package org.aswing
{
	import org.aswing.geom.IntDimension;
	import org.aswing.Container;
	import org.aswing.Component;
	
/**
 * LayoutManager's empty implementation.
 * @author iiley
 */
public class EmptyLayout implements LayoutManager
{
	public function EmptyLayout(){
	}
	
    /** 
     * Do nothing
     * @inheritDoc
     */
    public function addLayoutComponent(comp:Component, constraints:Object):void{
    }

    /**
     * Do nothing
     * @inheritDoc
     */
    public function removeLayoutComponent(comp:Component):void{
    }
	
	/**
	 * Simply return target.getSize();
	 */
    public function preferredLayoutSize(target:Container):IntDimension{
    	return target.getSize();
    }

	/**
	 * new IntDimension(0, 0);
	 */
    public function minimumLayoutSize(target:Container):IntDimension{
    	return new IntDimension(0, 0);
    }
	
	/**
	 * return IntDimension.createBigDimension();
	 */
    public function maximumLayoutSize(target:Container):IntDimension{
    	return IntDimension.createBigDimension();
    }
    
    /**
     * do nothing
     */
    public function layoutContainer(target:Container):void{
    }
    
	/**
	 * return 0
	 */
    public function getLayoutAlignmentX(target:Container):Number{
    	return 0;
    }

	/**
	 * return 0
	 */
    public function getLayoutAlignmentY(target:Container):Number{
    	return 0;
    }

    /**
     * do nothing
     */
    public function invalidateLayout(target:Container):void{
    }		
	
}
}