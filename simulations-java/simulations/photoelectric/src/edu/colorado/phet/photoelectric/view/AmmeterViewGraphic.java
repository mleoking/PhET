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

import java.awt.*;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.photoelectric.PhotoelectricResources;
import edu.colorado.phet.photoelectric.model.Ammeter;
import edu.colorado.phet.photoelectric.model.PhotoelectricModel;

/**
 * AmmeterView
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AmmeterViewGraphic extends CompositePhetGraphic {

    private Font font = new PhetFont( Font.BOLD, 14 );
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
        currentLabel = new PhetTextGraphic( component, font, PhotoelectricResources.getString( "GraphLabel.Current" )+": ", Color.black );
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
                // Do this to keep "-0.000" from being displayed  
                if( current == 0 ) {
                    current = 0;
                }
                currentTF.setText( format.format( current ) );
                AmmeterViewGraphic.this.setBoundsDirty();
                AmmeterViewGraphic.this.repaint();
            }
        } );
    }
}
