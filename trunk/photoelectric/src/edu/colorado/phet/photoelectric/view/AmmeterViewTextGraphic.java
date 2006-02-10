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

import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.photoelectric.model.Ammeter;
import edu.colorado.phet.photoelectric.model.util.ScalarDataRecorder;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * AmmeterView
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AmmeterViewTextGraphic extends PhetTextGraphic {

    static private Font font = new Font( "Lucida Sans", Font.PLAIN, 6 );
    private PhetTextGraphic currentLabel;
    private PhetTextGraphic currentTF;
    private DecimalFormat format = new DecimalFormat( "#0.0000" );

    public AmmeterViewTextGraphic( Component component, final Ammeter ammeter ) {
        super( component, font, "Current", Color.black );
//        currentLabel = new PhetTextGraphic( component, font, "Current", Color.black );
//        currentTF = new PhetTextGraphic( component, font, "0.0000", Color.black );
//        currentTF.setLocation( 50, 0 );
//        this.addGraphic( currentLabel );
//        this.addGraphic( currentTF );

        ammeter.addUpdateListener( new ScalarDataRecorder.UpdateListener() {
            public void update( ScalarDataRecorder.UpdateEvent event ) {
                double current = ammeter.getCurrent();
                currentTF.setText( format.format( current ));
                AmmeterViewTextGraphic.this.setBoundsDirty();
                AmmeterViewTextGraphic.this.repaint();
            }
        } );
    }
}
