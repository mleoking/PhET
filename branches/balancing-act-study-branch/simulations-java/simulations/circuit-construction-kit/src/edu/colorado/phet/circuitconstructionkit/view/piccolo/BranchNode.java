// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.*;

import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Sep 18, 2006
 * Time: 11:15:28 AM
 */
public abstract class BranchNode extends PhetPNode {
    public abstract Branch getBranch();

    public abstract void delete();

    public Shape getClipShape( PNode parent ) {
        return null;
    }

    public ParameterSet getPositionParameterSet() {
        return ParameterSet.parameterSet( ParameterKeys.x, getBranch().getX1() ).with( ParameterKeys.y, getBranch().getY1() ).with( ParameterKeys.x2, getBranch().getX2() ).with( ParameterKeys.y2, getBranch().getY2() );
    }
}
