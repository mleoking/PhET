// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * Context implemented by TugOfWarCanvas and used by PullerNode to reduce coupling between the two.
 *
 * @author Sam Reid
 */
public interface PullerContext {
    void drag( PullerNode pullerNode );

    void endDrag( PullerNode pullerNode );

    void startDrag( PullerNode pullerNode );

    boolean isCartInCenter();

    void addCartPositionChangeListener( VoidFunction0 voidFunction0 );
}