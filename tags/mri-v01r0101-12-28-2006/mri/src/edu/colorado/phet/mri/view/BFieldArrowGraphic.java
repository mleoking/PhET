/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/**
 * BFieldArrowGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BFieldArrowGraphic extends PhetPCanvas {
    private RegisterablePNode indicatorGraphic;
    private double maxArrowFractionOfHeight;
//    private JTextField readout;
    private DecimalFormat readoutFormat = new DecimalFormat( "0.00" );
    private Point2D samplePoint = new Point2D.Double();

    /**
     * Constructor
     *
     * @param model
     * @param minLength
     */
    public BFieldArrowGraphic( MriModel model, double minLength ) {

        setPreferredSize( new Dimension( 180, 80 ) );
        maxArrowFractionOfHeight = 0.9;
        final BFieldIndicator indicator = new BFieldIndicator( model,
                                                               samplePoint,
                                                               getPreferredSize().getHeight() * maxArrowFractionOfHeight,
                                                               new Color( 80, 80, 180 ) );
        indicator.setMinLength( minLength );
        indicatorGraphic = new RegisterablePNode( indicator );
        addWorldChild( indicatorGraphic );

        // Text readout for field
//        readout = new JTextField( 6 );
//        readout.setHorizontalAlignment( JTextField.CENTER );
//        final PSwing readoutPSwing = new PSwing( this, readout );
//        readoutPSwing.setOffset( getWidth() - 70, getHeight() / 2 );
//        addWorldChild( readoutPSwing );
//        updateReadout( model );

        // When the panel is resized (or first displayed) update the placement of the arrow
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateRegistrationPoint();
                indicator.setMaxLength( getHeight() * maxArrowFractionOfHeight );
//                readoutPSwing.setOffset( getWidth() - 70, getHeight() / 2 );
            }
        } );

        // Hook up listeners to the model
//        model.addListener( new MriModel.ChangeAdapter() {
//            public void fieldChanged( MriModel model ) {
//                updateReadout( model );
//            }
//        } );

        samplePoint.setLocation( model.getBounds().getCenterX(), model.getBounds().getCenterY() );
    }

//    private void updateReadout( MriModel model ) {
//        double fieldStrength = model.getTotalFieldStrengthAt( samplePoint );
//        String valueStr = readoutFormat.format( fieldStrength );
//        readout.setText( valueStr + " Tesla" );
//    }

    private void updateRegistrationPoint() {
        indicatorGraphic.setOffset( getWidth() / 2, getHeight() / 2 );
        indicatorGraphic.setRegistrationPoint( indicatorGraphic.getWidth() / 2, indicatorGraphic.getHeight() / 2 );
    }
}
