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
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * AmmeterView
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AmmeterViewGraphic extends CompositePhetGraphic {
//public class AmmeterViewGraphic extends CompositePhetGraphic {

    private Font font = new Font( "Lucida Sans", Font.PLAIN, 12 );
    private PhetShapeGraphic background;
    private PhetTextGraphic currentLabel;
    private PhetTextGraphic currentTF;
    private DecimalFormat format = new DecimalFormat( "#0.0000" );

    public AmmeterViewGraphic( Component component, final Ammeter ammeter ) {
        background = new PhetShapeGraphic( component,
                                           new Rectangle( 105, 20 ),
                                           Color.white,
                                           new BasicStroke( 1f ),
                                           Color.black );
        currentLabel = new PhetTextGraphic( component, font, "Current: ", Color.black );
        currentLabel.setLocation( 5, 5 );
        currentTF = new PhetTextGraphic( component, font, "0.0000", Color.black );
        currentTF.setLocation( 55, 5 );
        this.addGraphic( background );
        this.addGraphic( currentLabel );
        this.addGraphic( currentTF );

        ammeter.addUpdateListener( new ScalarDataRecorder.UpdateListener() {
            public void update( ScalarDataRecorder.UpdateEvent event ) {
                double current = ammeter.getCurrent();
                currentTF.setText( format.format( current ) );
                AmmeterViewGraphic.this.setBoundsDirty();
                AmmeterViewGraphic.this.repaint();
            }
        } );
    }
}
