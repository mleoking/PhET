// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for all straight line nodes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class StraightLineNode extends PComposite {

    public final StraightLine line;

    public StraightLineNode( StraightLine line ) {
         this.line = line;
    }

    public abstract void setEquationVisible( boolean visible );
}
