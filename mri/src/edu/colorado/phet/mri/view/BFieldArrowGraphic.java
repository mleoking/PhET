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

import edu.colorado.phet.mri.model.Electromagnet;
import edu.colorado.phet.mri.model.GradientElectromagnet;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
    private JTextField readout;
    private DecimalFormat readoutFormat = new DecimalFormat( "0.00" );

    /**
     * Constructor
     * @param magnet
     * @param minLength
     */
    public BFieldArrowGraphic( GradientElectromagnet magnet, double minLength ) {

        setPreferredSize( new Dimension( 150, 150 ) );
        maxArrowFractionOfHeight = 0.9;
        final BFieldIndicator indicator = new BFieldIndicator( magnet,
                                                               getPreferredSize().getHeight() * maxArrowFractionOfHeight,
                                                               new Color( 80, 80, 180 ),
                                                               0 );
        indicator.setMinLength( minLength );
        indicatorGraphic = new RegisterablePNode( indicator );
        addWorldChild( indicatorGraphic );

        // Text readout for field
        readout = new JTextField( 6 );
        readout.setHorizontalAlignment( JTextField.CENTER );
        final PSwing readoutPSwing = new PSwing( this, readout );
        readoutPSwing.setOffset( getWidth() - 50, getHeight() / 2 );
        readoutPSwing.setOffset( getWidth() - 50, getHeight() / 2 );
        addWorldChild( readoutPSwing );
        updateReadout( magnet );

        // When the panel is resized (or first displayed) update the placement of the arror
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateRegistrationPoint();
                indicator.setMaxLength( getHeight() * maxArrowFractionOfHeight );
                readoutPSwing.setOffset( getWidth() - 70, getHeight() / 2 );
            }
        } );

        // Hook up listeners to the model
        magnet.addChangeListener( new Electromagnet.ChangeListener() {
            public void stateChanged( Electromagnet.ChangeEvent event ) {
                updateRegistrationPoint();
                updateReadout( event.getElectromagnet() );
            }
        } );
    }

    /**
     * Constructor
     * @param magnet
     */
    public BFieldArrowGraphic( GradientElectromagnet magnet ) {
        this( magnet, 0 );
    }

    private void updateReadout( Electromagnet magnet ) {
        String valueStr = readoutFormat.format( magnet.getFieldStrength() );
        readout.setText( valueStr + " Tesla" );
    }

    private void updateRegistrationPoint() {
        indicatorGraphic.setOffset( getWidth() / 2, getHeight() / 2 );
        indicatorGraphic.setRegistrationPoint( indicatorGraphic.getWidth() / 2, indicatorGraphic.getHeight() / 2 );
    }
}
