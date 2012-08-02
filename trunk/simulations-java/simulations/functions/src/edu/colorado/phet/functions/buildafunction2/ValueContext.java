package edu.colorado.phet.functions.buildafunction2;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public interface ValueContext {
    void mouseDragged( final ValueNode valueNode, PDimension delta );

    void mouseReleased( final ValueNode valueNode );
}