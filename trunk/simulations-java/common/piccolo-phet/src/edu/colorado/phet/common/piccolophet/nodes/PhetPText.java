// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.Font;

import edu.umd.cs.piccolo.nodes.PText;

/**
 * Convenience class for creating a PText with the specified text and font
 *
 * @author Sam Reid
 */
public class PhetPText extends PText {
    public PhetPText( String text, Font font ) {
        super( text );
        setFont( font );
    }
}