package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 18, 2006
 * Time: 11:15:28 AM
 *
 */
public abstract class BranchNode extends PhetPNode {
    public abstract Branch getBranch();

    public abstract void delete();

    public Shape getClipShape( PNode parent ) {
        return null;
    }
}
