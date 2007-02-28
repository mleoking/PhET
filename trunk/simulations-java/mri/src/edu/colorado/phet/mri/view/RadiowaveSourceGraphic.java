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

import edu.colorado.phet.common.util.PhysicsUtil;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.controller.AbstractMriModule;
import edu.colorado.phet.mri.controller.EmRepSelector;
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.model.RadiowaveSource;
import edu.colorado.phet.mri.util.GraphicPSwing;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.colorado.phet.quantum.model.PhotonSource;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
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

    private double SQUIGGLE_LENGTH_CALIBRATION_FACTOR = 1.21E8 * 2.45 * .7; // for scale = 0.4

    private Font font = new Font( "Lucida Sans", Font.BOLD, 16 );
    private double panelDepth = 93;
    private EnergySquiggle energySquiggle;
    private MriModel model;

    public RadiowaveSourceGraphic( final RadiowaveSource radiowaveSource, PhetPCanvas canvas, AbstractMriModule module ) {

        model = (MriModel)module.getModel();

        // todo: this line and variable is just for debugging
        double length = 700;
//        double length = 600;

        double w = 0;
        double h = 0;
        double x = 0;
        double y = 0;
        if( radiowaveSource.getOrientation() == RadiowaveSource.HORIZONTAL ) {
            w = length;
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

//        Rectangle2D box = new Rectangle2D.Double( 0, 0, length, h );
//        PPath boxGraphic = new PPath( box );
//        boxGraphic.setPaint( new Color( 80, 80, 80 ) );
//        addChild( boxGraphic );

        // Background for the entire control
        PImage background = PImageFactory.create( "images/radiowave-control-background.png", new Dimension( (int)length, (int)h ) );
        addChild( background );

        // Frequency control
        Insets controlInsets = new Insets( 5, 5, 5, 5 );
        final ModelSlider freqCtrl = new ModelSlider( SimStrings.get( "Misc.Frequency" ),
                                                      "MHz",
                                                      MriConfig.MIN_FEQUENCY,
                                                      MriConfig.MAX_FEQUENCY,
                                                      MriConfig.MIN_FEQUENCY + ( MriConfig.MAX_FEQUENCY - MriConfig.MIN_FEQUENCY ) / 2,
                                                      new DecimalFormat( "0.0" ),
                                                      new DecimalFormat( "0" ) );
        freqCtrl.setNumMajorTicks( 5 );
        freqCtrl.setFont( font );
        {
            JTextField unitsReadout = freqCtrl.getUnitsReadout();
            Font orgFont = unitsReadout.getFont();
            Font newFont = new Font( orgFont.getName(), Font.PLAIN, orgFont.getSize() );
            unitsReadout.setFont( newFont );
        }
        freqCtrl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                radiowaveSource.setFrequency( freqCtrl.getValue() * MriConfig.FREQUENCY_UNIT );
            }
        } );
        radiowaveSource.setFrequency( freqCtrl.getValue() * MriConfig.FREQUENCY_UNIT );
        final PNode freqPSwing = new GraphicPSwing( new PSwing( canvas, freqCtrl ), "images/control-background.png" );
        freqPSwing.setOffset( length - controlInsets.right - freqPSwing.getBounds().getWidth(),
                              controlInsets.top );
        freqCtrl.getTextField().setOpaque( true );
        addChild( freqPSwing );
        freqCtrl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                freqPSwing.repaint();
            }
        } );

        // Squiggle next to frequency control
        if( module.auxiliarySquiggleVisible() ) {
            addAuxiliarySquiggle( length, controlInsets, freqPSwing, radiowaveSource );
        }

        // Power control
        final ModelSlider powerCtrl = new ModelSlider( SimStrings.get( "Misc.Power" ),
                                                       "%",
                                                       0,
                                                       MriConfig.MAX_POWER,
                                                       0,
                                                       new DecimalFormat( "0" ) );
        {
            JTextField unitsReadout = powerCtrl.getUnitsReadout();
            Font orgFont = unitsReadout.getFont();
            Font newFont = new Font( orgFont.getName(), Font.PLAIN, orgFont.getSize() );
            unitsReadout.setFont( newFont );
        }
        powerCtrl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                radiowaveSource.setPower( powerCtrl.getValue() );
            }
        } );
        powerCtrl.setValue( powerCtrl.getValue() );
        PNode powerPSwing = new GraphicPSwing( new PSwing( canvas, powerCtrl ), "images/control-background.png" );
        powerCtrl.getTextField().setOpaque( true );
        powerPSwing.setOffset( controlInsets.left, controlInsets.top );
        addChild( powerPSwing );

        // Controls for the photon/wave view choice
        EmRepSelector emRepSelector = new EmRepSelector( module );
        PNode emRepPSwing = new GraphicPSwing( new PSwing( canvas, emRepSelector ), "images/radio-button-background.png" );
        emRepPSwing.setOffset( ( length - emRepPSwing.getBounds().getWidth() ) / 2,
                               panelDepth - controlInsets.bottom - emRepPSwing.getBounds().getHeight() );
        addChild( emRepPSwing );

        // Label
        PText title = new PText( SimStrings.get( "Misc.RadiowaveSourceLabel" ) );
        title.setPaint( new Color( 0, 0, 0, 0 ) );
        title.setTextPaint( Color.white );
        title.setFont( font );
        title.setJustification( javax.swing.JLabel.CENTER_ALIGNMENT );
        title.setOffset( length / 2 - title.getBounds().getWidth() / 2, 10 );
        addChild( title );

        // Update the sliders if the radiowave source changes its state through some mechanism other than our
        // sliders
        radiowaveSource.addRateChangeListener( new PhotonSource.RateChangeListener() {
            public void rateChangeOccurred( PhotonSource.RateChangeEvent event ) {
                powerCtrl.setValue( ( (RadiowaveSource)event.getSource() ).getPower() );
            }
        } );
        radiowaveSource.addWavelengthChangeListener( new PhotonSource.WavelengthChangeListener() {
            public void wavelengthChanged( PhotonSource.WavelengthChangeEvent event ) {
                freqCtrl.setValue( PhysicsUtil.wavelengthToFrequency( ((RadiowaveSource)event.getSource()).getWavelength() ) );
            }
        } );
    }

    private void addAuxiliarySquiggle( double length, Insets controlInsets, PNode freqPSwing, RadiowaveSource radiowaveSource ) {
        energySquiggle = new EnergySquiggle( EnergySquiggle.VERTICAL );
        final PPath squiggleBox = new PPath( new Rectangle2D.Double( 0, 0, 15, 80 ) );
        squiggleBox.setPaint( Color.white );
        squiggleBox.setStrokePaint( Color.black );
        final PNode squiggleNode = new PNode();
        squiggleNode.addChild( squiggleBox );
        squiggleNode.addChild( energySquiggle );
        squiggleNode.setOffset( length - controlInsets.right * 2 - freqPSwing.getWidth() - squiggleBox.getWidth(),
                                controlInsets.top );
        addChild( squiggleNode );
        final EnergySquiggleUpdater energySquiggleUpdater = new EnergySquiggleUpdater( energySquiggle,
                                                                                       model );
        radiowaveSource.addRateChangeListener( new PhotonSource.RateChangeListener() {
            public void rateChangeOccurred( PhotonSource.RateChangeEvent event ) {
                double xOffset = ( squiggleBox.getBounds().getWidth() - energySquiggle.getFullBounds().getWidth() ) / 2;
                double yOffset = squiggleBox.getBounds().getHeight();
                energySquiggleUpdater.updateSquiggle( xOffset, yOffset, SQUIGGLE_LENGTH_CALIBRATION_FACTOR );
            }
        } );
        radiowaveSource.addWavelengthChangeListener( new PhotonSource.WavelengthChangeListener() {
            public void wavelengthChanged( PhotonSource.WavelengthChangeEvent event ) {
                double xOffset = ( squiggleBox.getBounds().getWidth() - energySquiggle.getFullBounds().getWidth() ) / 2;
                double yOffset = squiggleBox.getBounds().getHeight();
                energySquiggleUpdater.updateSquiggle( xOffset, yOffset, SQUIGGLE_LENGTH_CALIBRATION_FACTOR );
            }
        } );
        model.addListener( new MriModel.ChangeAdapter() {
            public void fieldChanged( MriModel model ) {
                double xOffset = ( squiggleBox.getBounds().getWidth() - energySquiggle.getFullBounds().getWidth() ) / 2;
                double yOffset = squiggleBox.getBounds().getHeight();
                energySquiggleUpdater.updateSquiggle( xOffset, yOffset, SQUIGGLE_LENGTH_CALIBRATION_FACTOR );
            }
        } );
    }
}
