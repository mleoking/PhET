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

import edu.colorado.phet.common.phetcommon.util.PhysicsUtil;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.colorado.phet.common.quantum.model.PhotonSource;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.controller.AbstractMriModule;
import edu.colorado.phet.mri.controller.EmRepSelector;
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.model.RadiowaveSource;
import edu.colorado.phet.mri.util.GraphicPSwing;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
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

    private Font font = new PhetFont( Font.BOLD, 16 );
    private double panelDepth = 93;
    private EnergySquiggle energySquiggle;
    private MriModel model;
    private RadiowaveSource radiowaveSource;
    private ModelSlider frequencySlider;

    public RadiowaveSourceGraphic( final RadiowaveSource radiowaveSource, final AbstractMriModule module ) {
        model = (MriModel)module.getModel();
        this.radiowaveSource = radiowaveSource;

        // todo: this line and variable is just for debugging
        double length = 700;

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

        // Background for the entire control
        PImage background = PImageFactory.create( "mri/images/radiowave-control-background.png", new Dimension( (int)length, (int)h ) );
        addChild( background );

        // Frequency control
        Insets controlInsets = new Insets( 5, 5, 5, 5 );
        frequencySlider = new ModelSlider( SimStrings.getInstance().getString( "Misc.Frequency" ),
                                           SimStrings.getInstance().getString( "Misc.Frequency.Units" ),
                                           MriConfig.MIN_FEQUENCY,
                                           MriConfig.MAX_FEQUENCY,
                                           MriConfig.MIN_FEQUENCY + ( MriConfig.MAX_FEQUENCY - MriConfig.MIN_FEQUENCY ) / 2,
                                           new DecimalFormat( "0.0" ),
                                           new DecimalFormat( "0" ) );
        frequencySlider.setNumMajorTicks( 5 );
        frequencySlider.setFont( font );
        {
            JTextField unitsReadout = frequencySlider.getUnitsReadout();
            Font orgFont = unitsReadout.getFont();
            Font newFont = new Font( orgFont.getName(), Font.PLAIN, orgFont.getSize() );
            unitsReadout.setFont( newFont );
        }
        frequencySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                radiowaveSource.setFrequency( frequencySlider.getValue() * MriConfig.FREQUENCY_UNIT );
            }
        } );
        frequencySlider.getSlider().addMouseListener( new MouseInputAdapter() {
            public void mouseReleased( MouseEvent e ) {
                if( module.getMriModel().isTransitionMatch() ) {
                    double frequency = module.getMriModel().getMatchingFrequency();
                    radiowaveSource.setFrequency( frequency );
                    updateFrequencySliderValue();
                }
            }
        } );
        radiowaveSource.setFrequency( frequencySlider.getValue() * MriConfig.FREQUENCY_UNIT );
        final PNode freqPSwing = new GraphicPSwing( new PSwing( frequencySlider ), "mri/images/control-background.png" );
        freqPSwing.setOffset( length - controlInsets.right - freqPSwing.getBounds().getWidth(),
                              controlInsets.top );
        frequencySlider.getTextField().setOpaque( true );
        addChild( freqPSwing );
        frequencySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                freqPSwing.repaint();
            }
        } );

        // Squiggle next to frequency control
        if( module.auxiliarySquiggleVisible() ) {
            addAuxiliarySquiggle( length, controlInsets, freqPSwing, radiowaveSource );
        }

        // Power control
        final ModelSlider powerCtrl = new ModelSlider( SimStrings.getInstance().getString( "Misc.Power" ),
                                                       SimStrings.getInstance().getString( "percent" ),
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
        PNode powerPSwing = new GraphicPSwing( new PSwing( powerCtrl ), "mri/images/control-background.png" );
        powerCtrl.getTextField().setOpaque( true );
        powerPSwing.setOffset( controlInsets.left, controlInsets.top );
        addChild( powerPSwing );

        // Controls for the photon/wave view choice
        EmRepSelector emRepSelector = new EmRepSelector( module );
        PNode emRepPSwing = new GraphicPSwing( new PSwing( emRepSelector ), "mri/images/radio-button-background.png" );
        emRepPSwing.setOffset( ( length - emRepPSwing.getBounds().getWidth() ) / 2,
                               panelDepth - controlInsets.bottom - emRepPSwing.getBounds().getHeight() );
        addChild( emRepPSwing );

        // Label
        PText title = new PText( SimStrings.getInstance().getString( "Misc.RadiowaveSourceLabel" ) );
        title.setPaint( new Color( 0, 0, 0, 0 ) );
        title.setTextPaint( Color.white );
        title.setFont( font );
        title.setJustification( javax.swing.JLabel.CENTER_ALIGNMENT );
        title.setOffset( length / 2 - title.getBounds().getWidth() / 2, 10 );
        addChild( title );

        // Update the sliders if the radiowave source changes its state through some mechanism other than our sliders
        radiowaveSource.addRateChangeListener( new PhotonSource.RateChangeListener() {
            public void rateChangeOccurred( PhotonSource.RateChangeEvent event ) {
                powerCtrl.setValue( ( (RadiowaveSource)event.getSource() ).getPower() );
            }
        } );
        radiowaveSource.addWavelengthChangeListener( new PhotonSource.WavelengthChangeListener() {
            public void wavelengthChanged( PhotonSource.WavelengthChangeEvent event ) {
                updateFrequencySliderValue();
            }
        } );
    }

    private void updateFrequencySliderValue() {
        frequencySlider.setValue( PhysicsUtil.wavelengthToFrequency( radiowaveSource.getWavelength() ) / MriConfig.FREQUENCY_UNIT );
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
        final EnergySquiggleUpdater energySquiggleUpdater = new EnergySquiggleUpdater( energySquiggle, model );
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
