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

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.photoelectric.model.Ammeter;
import edu.colorado.phet.photoelectric.model.PhotoelectricModel;

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

    private Font font = new Font( "Lucida Sans", Font.BOLD, 14 );
    private PhetShapeGraphic background1;
    private PhetShapeGraphic background2;
    private PhetTextGraphic currentLabel;
    private PhetTextGraphic currentTF;
    private DecimalFormat format = new DecimalFormat( "#0.000" );

    public AmmeterViewGraphic( Component component, final Ammeter ammeter, final PhotoelectricModel model ) {
        background1 = new PhetShapeGraphic( component,
                                           new Rectangle( 120, 20 ),
                                           Color.white,
                                           new BasicStroke( 1f ),
                                           Color.black );
        background2 = new PhetShapeGraphic( component,
                                           new Rectangle( 130, 30 ),
                                           Color.yellow,
                                           new BasicStroke( 1f ),
                                           Color.black );
        background2.setRegistrationPoint( 5, 5 );
        currentLabel = new PhetTextGraphic( component, font, "Current: ", Color.black );
        currentLabel.setLocation( 5, 5 );
        currentTF = new PhetTextGraphic( component, font, "0.000", Color.black );
        currentTF.setLocation( 75, 5 );
        this.addGraphic( background2 );
        this.addGraphic( background1 );
        this.addGraphic( currentLabel );
        this.addGraphic( currentTF );

//        ammeter.addUpdateListener( new ScalarDataRecorder.UpdateListener() {
//            public void update( ScalarDataRecorder.UpdateEvent event ) {
//                double current = ammeter.getCurrent();
//                currentTF.setText( format.format( current ) );
//                AmmeterViewGraphic.this.setBoundsDirty();
//                AmmeterViewGraphic.this.repaint();
//            }
//        } );

        model.addChangeListener( new PhotoelectricModel.ChangeListenerAdapter() {
            public void currentChanged( PhotoelectricModel.ChangeEvent event ) {
                double current = model.getCurrent();
                currentTF.setText( format.format( current ) );
                AmmeterViewGraphic.this.setBoundsDirty();
                AmmeterViewGraphic.this.repaint();
            }
        });
    }
}
