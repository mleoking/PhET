/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.view;

import edu.colorado.phet.photoelectric.model.Ammeter;
import edu.colorado.phet.photoelectric.model.util.ScalarDataRecorder;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * AmmeterView
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AmmeterView extends JPanel {

    private JTextField currentTF;
    private DecimalFormat format = new DecimalFormat( "#0.0000" );

    public AmmeterView( final Ammeter ammeter ) {
        this(ammeter, true );
    }

    public AmmeterView( final Ammeter ammeter, boolean horizontal ) {
        LayoutManager layout = horizontal ? new GridLayout( 1, 2 ) : new GridLayout( 2, 1 );
        JPanel currentPanel = new JPanel( layout );
        currentPanel.add( new JLabel( "Current: "));
        currentTF = new JTextField( 6 );
        currentPanel.add( currentTF );
        add( currentPanel );

        ammeter.addUpdateListener( new ScalarDataRecorder.UpdateListener() {
            public void update( ScalarDataRecorder.UpdateEvent event ) {
                double current = ammeter.getCurrent();
                currentTF.setText( format.format( current ));
            }
        } );
    }
}
