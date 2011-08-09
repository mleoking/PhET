// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.kit;

import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.umd.cs.piccolo.PNode;

/**
 * Convenience class that contains the kit's content node (which should
 * contain the individual elements of the kit) as well as a title.  These are
 * separate so that they can be laid out separately.
 *
 * @author John Blanco
 */
public class Kit<T extends PNode> {
    public final Option<PNode> title;
    public final T content;

    public Kit( T content ) {
        this( new None<PNode>(), content );
    }

    public Kit( PNode title, T content ) {
        this( new Some<PNode>( title ), content );
    }

    public Kit( Option<PNode> title, T content ) {
        this.title = title;
        this.content = content;
    }
}
