// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;

import edu.colorado.phet.common.piccolophet.PhetPNode;

/**
 * Base class for all equation nodes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class EquationNode extends PhetPNode {

    public abstract void setEquationColor( Color color );
}
