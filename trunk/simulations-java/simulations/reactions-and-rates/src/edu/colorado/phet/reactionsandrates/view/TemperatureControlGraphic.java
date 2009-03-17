/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.view;

import edu.colorado.phet.common.piccolophet.nodes.RegisterablePNode;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.model.TemperatureControl;
import edu.colorado.phet.reactionsandrates.util.ControlBorderFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Hashtable;

/**
 * TemperatureControlGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TemperatureControlGraphic extends RegisterablePNode implements TemperatureControl.ChangeListener {
    private PSwingCanvas canvas;
    private TemperatureControl temperatureControl;
    private JSlider stoveSlider;
    private PImage stoveGraphic;
    private PImage flamesGraphic;
    private PImage iceGraphic;
    private double stoveOffsetY = 25;
    private volatile boolean adjustmentInProgress = false;

    public TemperatureControlGraphic( PSwingCanvas canvas, TemperatureControl tempCtrl ) {
        this.canvas = canvas;
        this.temperatureControl = tempCtrl;
        tempCtrl.addChangeListener( this );

        // The stove graphic
        createImages( stoveOffsetY );

        // The slider
        PNode sliderNode = createSlider();

        addChild( sliderNode );
        setRegistrationPoint( stoveGraphic.getWidth() / 2, 0 );

        setOffset( tempCtrl.getPosition() );
    }

    private void createImages( double baseOffsetY ) {
        // Set up the stove, flames, and ice
        flamesGraphic = PImageFactory.create( MRConfig.FLAMES_IMAGE_FILE );
        addChild( flamesGraphic );
        iceGraphic = PImageFactory.create( MRConfig.ICE_IMAGE_FILE );
        addChild( iceGraphic );
        stoveGraphic = PImageFactory.create( MRConfig.STOVE_IMAGE_FILE );
        stoveGraphic.setOffset( 0, baseOffsetY );
        addChild( stoveGraphic );
        iceGraphic.setOffset( ( stoveGraphic.getWidth() - iceGraphic.getWidth() ) / 2, baseOffsetY );
        flamesGraphic.setOffset( ( stoveGraphic.getWidth() - flamesGraphic.getWidth() ) / 2, baseOffsetY );
    }

    class MyJLabel extends JLabel {
        public MyJLabel( String text ) {
            super( text );
        }

        public Dimension getPreferredSize() {
            return super.getPreferredSize();
        }
    }

    private PNode createSlider() {
        int maxStoveSliderValue = 40;

        stoveSlider = new JSlider( JSlider.VERTICAL, -maxStoveSliderValue,
                                   maxStoveSliderValue, 0 );
        stoveSlider.setMajorTickSpacing( maxStoveSliderValue );
        stoveSlider.setMinorTickSpacing( 10 );
        stoveSlider.setSnapToTicks( true );
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 0 ), new JLabel( MRConfig.RESOURCES.getLocalizedString( "Control.0" ) ) );
        labelTable.put( new Integer( -40 ), new MyJLabel( MRConfig.RESOURCES.getLocalizedString( "Control.Lower" ) ) );
        labelTable.put( new Integer( 40 ), new MyJLabel( MRConfig.RESOURCES.getLocalizedString( "Control.Raise" ) ) );
        stoveSlider.setLabelTable( labelTable );
        stoveSlider.setPaintTicks( true );
        stoveSlider.setSnapToTicks( true );
        stoveSlider.setPaintLabels( true );
        stoveSlider.setPreferredSize( new Dimension( 100, 60 ) );
        stoveSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                if( stoveSlider.getValueIsAdjusting() ) {
                    adjustmentInProgress = true;
                }
                else {
                    adjustmentInProgress = false;
                }

                temperatureControl.setSetting( stoveSlider.getValue() );
            }
        } );

        stoveSlider.addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                stoveSlider.setValue( 0 );
            }

            public void mousePressed( MouseEvent e ) {
                super.mousePressed( e );
            }
        } );

        stoveSlider.setBorder( ControlBorderFactory.createPrimaryBorder( MRConfig.RESOURCES.getLocalizedString( "Control.Heat_Control" ) ) );
        Color background = MRConfig.SPATIAL_VIEW_BACKGROUND;
        stoveSlider.setBackground( background );

        stoveSlider.setPreferredSize( new Dimension( stoveSlider.getPreferredSize().width, 100 ) );
        PSwing sliderNode = new PSwing( stoveSlider );
        sliderNode.setOffset( stoveGraphic.getWidth() + 5, 0 );

        return sliderNode;
    }

    public boolean isTemperatureBeingAdjusted() {
        return adjustmentInProgress;
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of TemperatureControl.ChangeListener
    //--------------------------------------------------------------------------------------------------

    public void settingChanged( double setting ) {
        stoveSlider.setValue( (int)setting );
        double flameHeight = stoveOffsetY - setting;
        double iceHeight = stoveOffsetY + setting;
        flamesGraphic.setOffset( flamesGraphic.getOffset().getX(),
                                 (int)Math.min( flameHeight, stoveOffsetY ) );
        iceGraphic.setOffset( (int)iceGraphic.getOffset().getX(),
                              (int)Math.min( iceHeight, stoveOffsetY ) );
    }

    public void positionChanged( Point2D newPosition ) {
        setOffset( newPosition );
    }
}
