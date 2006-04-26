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

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.RadiowaveSource;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

/**
 * RadiowaveSourceGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RadiowaveSourceGraphic extends PNode {

    Font font = new Font( "Lucida Sans", Font.BOLD, 16 );
    private double panelDepth = 90;

    public RadiowaveSourceGraphic( final RadiowaveSource radiowaveSource, PhetPCanvas canvas ) {

        // todo: this line and variable is just for debugging
        double length = 600;
//        double length = 800;
//        double length = radiowaveSource.getLength();

        double w = 0;
        double h = 0;
        double x = 0;
        double y = 0;
        if( radiowaveSource.getOrientation() == RadiowaveSource.HORIZONTAL ) {
            w = length;
//            w = radiowaveSource.getLength();
            h = panelDepth;
            x = radiowaveSource.getPosition().getX() - w / 2;
            y = radiowaveSource.getPosition().getY();
        }
        else if( radiowaveSource.getOrientation() == RadiowaveSource.VERTICAL ) {
            w = panelDepth;
            h = length;
            x = radiowaveSource.getPosition().getX();
            y = radiowaveSource.getPosition().getY() + h / 2;
        }
        setOffset( x, y );

        Rectangle2D box = new Rectangle2D.Double( 0, 0, length, h );
//        Rectangle2D box = new Rectangle2D.Double( 0, 0, w, h );
        PPath boxGraphic = new PPath( box );
        boxGraphic.setPaint( new Color( 80, 80, 80 ) );
        addChild( boxGraphic );

        // Frequency control
        final ModelSlider freqCtrl = new ModelSlider( "Frequency",
                                                      "Hz",
                                                      MriConfig.MIN_FEQUENCY,
                                                      MriConfig.MAX_FEQUENCY,
                                                      MriConfig.MIN_FEQUENCY,
                                                      new DecimalFormat( "0.0E0" ) );
        freqCtrl.setFont( font );
        freqCtrl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                radiowaveSource.setFrequency( freqCtrl.getValue() );
            }
        } );
        radiowaveSource.setFrequency( freqCtrl.getValue() );
        PSwing freqPSwing = new PSwing( canvas, freqCtrl );
        freqPSwing.setOffset( length * 0.2 - freqPSwing.getBounds().getWidth() / 2, 5 );
//        freqPSwing.setOffset( length / 4 - freqPSwing.getBounds().getWidth() / 2, 30 );
        addChild( freqPSwing );

        // Power control
        final ModelSlider powerCtrl = new ModelSlider( "Power",
                                                       "",
                                                       0,
                                                       MriConfig.MAX_POWER,
                                                       0,
                                                       new DecimalFormat( "0.0E0" ) );
        powerCtrl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                radiowaveSource.setPower( powerCtrl.getValue() );
            }
        } );
        powerCtrl.setValue( MriConfig.MAX_POWER / 2 );
        PSwing powerPSwing = new PSwing( canvas, powerCtrl );
        powerPSwing.setOffset( length * 0.8 - powerPSwing.getBounds().getWidth() / 2, 5 );
//        powerPSwing.setOffset( length * 3 / 4 - powerPSwing.getBounds().getWidth() / 2, 30 );
        addChild( powerPSwing );

        // Label
        PText title = new PText( "Radiowave\nSource" );
        title.setPaint( new Color( 0, 0, 0, 0 ) );
        title.setTextPaint( Color.white );
        title.setFont( font );
        title.setJustification( javax.swing.JLabel.CENTER_ALIGNMENT );
        title.setOffset( length / 2 - title.getBounds().getWidth() / 2, 10 );
//        title.setOffset( radiowaveSource.getLength() / 2 - title.getBounds().getWidth() / 2, 10 );
        addChild( title );
    }
}
