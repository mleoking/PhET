// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * @author Sam Reid
 */
public interface PullerContext {
    void drag( PullerNode pullerNode );

    void endDrag( PullerNode pullerNode );

    void startDrag( PullerNode pullerNode );

    boolean cartIsInCenter();

    void addCartPositionChangeListener( VoidFunction0 voidFunction0 );
}