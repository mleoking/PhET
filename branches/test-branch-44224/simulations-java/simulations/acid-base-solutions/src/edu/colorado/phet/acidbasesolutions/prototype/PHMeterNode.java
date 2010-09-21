/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.text.DecimalFormat;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umd.cs.piccolo.nodes.PText;


/**
 * A very simple pH "meter", simply displays the pH value.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PHMeterNode extends PText {

    private static final DecimalFormat PH_FORMAT = new DecimalFormat( "0.00" );
    
    private final WeakAcid solution;
    
    public PHMeterNode( WeakAcid solution ) {
        super( "?" );
        scale( 2.5 );
        this.solution = solution;
        solution.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        } );
        update();
    }
    
    private void update() {
        setText( "pH = " + PH_FORMAT.format( solution.getPH() ) );
    }
}
