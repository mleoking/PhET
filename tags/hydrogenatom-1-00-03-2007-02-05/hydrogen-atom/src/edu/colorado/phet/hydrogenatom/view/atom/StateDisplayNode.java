/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view.atom;

import java.awt.Color;
import java.awt.Font;
import java.text.MessageFormat;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.model.SchrodingerModel;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * StateDisplayNode is the node used to display an atom's state variables.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
class StateDisplayNode extends PText {

    private static final Font FONT = new Font( HAConstants.DEFAULT_FONT_NAME, Font.BOLD, 16 );
    private static final Color COLOR = Color.WHITE;
    
    private static final String N_FORMAT = "n = {0}";
    private static final String NLM_FORMAT = "(n,l,m) = ({0},{1},{2})";

    /**
     * Constructor.
     * @param atom
     */
    public StateDisplayNode() {
        super();
        setPickable( false );
        setChildrenPickable( false );

        setFont( FONT );
        setTextPaint( COLOR );
    }

    /*
     * Sets the display to show an "n" state.
     * 
     * @param n
     */
    public void setState( int n ) {
        Object[] args = { new Integer( n ) };
        String s = MessageFormat.format( N_FORMAT, args );
        setText( s );  
    }
    
    /*
     * Sets the display to show an "n,l,m" state.
     * 
     * @param n
     * @param l
     * @param m
     */
    public void setState( int n, int l, int m ) {
        Object[] args = { new Integer( n ), new Integer( l ), new Integer( m ) };
        String s = MessageFormat.format( NLM_FORMAT, args );
        setText( s );
    }
}
