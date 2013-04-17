// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.view;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
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

    static private Font font = new PhetFont( Font.PLAIN, 6 );
    private PhetTextGraphic currentLabel;
    private PhetTextGraphic currentTF;
    private DecimalFormat format = new DecimalFormat( "#0.0000" );

    public AmmeterViewTextGraphic( Component component, final Ammeter ammeter ) {
        super( component, font, "Current", Color.black );

        ammeter.addUpdateListener( new ScalarDataRecorder.UpdateListener() {
            public void update( ScalarDataRecorder.UpdateEvent event ) {
                double current = ammeter.getCurrent();
                currentTF.setText( format.format( current ) );
                AmmeterViewTextGraphic.this.setBoundsDirty();
                AmmeterViewTextGraphic.this.repaint();
            }
        } );
    }
}
