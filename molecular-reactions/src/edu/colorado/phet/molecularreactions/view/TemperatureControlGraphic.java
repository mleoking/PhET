/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.model.TemperatureControl;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    public TemperatureControlGraphic( PSwingCanvas canvas, TemperatureControl tempCtrl ) {
        this.canvas = canvas;
        this.temperatureControl = tempCtrl;
        tempCtrl.addChangeListener( this );

        // The stove graphic
        createImages();

        // The slider
        PNode sliderNode = createSlider();

        addChild( sliderNode );
        setRegistrationPoint( stoveGraphic.getWidth() / 2, 0 );
    }

    private void createImages() {
        // Set up the stove, flames, and ice
        flamesGraphic = PImageFactory.create( MRConfig.FLAMES_IMAGE_FILE );
        addChild( flamesGraphic );
        iceGraphic = PImageFactory.create( MRConfig.ICE_IMAGE_FILE );
        addChild( iceGraphic );
        stoveGraphic = PImageFactory.create( MRConfig.STOVE_IMAGE_FILE );
        stoveGraphic.setOffset( 0, 0 );
        addChild( stoveGraphic );
        iceGraphic.setOffset( ( stoveGraphic.getWidth() - iceGraphic.getWidth() ) / 2, 0 );
        flamesGraphic.setOffset( ( stoveGraphic.getWidth() - flamesGraphic.getWidth() ) / 2, 0 );

        // Add a rectangle that will mask the ice and flames when they are behind the stove
    }

    private PNode createSlider() {
        JPanel stovePanel = new JPanel();
        int maxStoveSliderValue = 40;
        stoveSlider = new JSlider( JSlider.VERTICAL, -maxStoveSliderValue,
                                   maxStoveSliderValue, 0 );
        stoveSlider.setMajorTickSpacing( maxStoveSliderValue );
        stoveSlider.setMinorTickSpacing( 10 );
        stoveSlider.setSnapToTicks( true );
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( -40 ), new JLabel( SimStrings.get( "Control.Remove" ) ) );
        labelTable.put( new Integer( 0 ), new JLabel( SimStrings.get( "Control.0" ) ) );
        labelTable.put( new Integer( 40 ), new JLabel( SimStrings.get( "Control.Add" ) ) );
        stoveSlider.setLabelTable( labelTable );
        stoveSlider.setPaintTicks( true );
        stoveSlider.setSnapToTicks( true );
        stoveSlider.setPaintLabels( true );
        stoveSlider.setPreferredSize( new Dimension( 100, 60 ) );
        stoveSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
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

        Border border = new TitledBorder( new EtchedBorder( BevelBorder.RAISED,
                                                            new Color( 40, 20, 255 ),
                                                            Color.black ),
                                          SimStrings.get( "Control.Heat_Control" ) );
        stovePanel.setBorder( border );
        stovePanel.setPreferredSize( new Dimension( 115, 85 ) );
        Color background = new Color( 240, 230, 255 );
        stovePanel.setBackground( background );
        stoveSlider.setBackground( background );

        PSwing sliderNode = new PSwing( canvas, stoveSlider );
        sliderNode.setOffset( stoveGraphic.getWidth() + 5, 0 );

        return sliderNode;
    }

    public void settingChanged( double setting ) {
        stoveSlider.setValue( (int)setting );

        int baseFlameHeight = 0;
        int flameHeight = baseFlameHeight - (int)setting;
        int iceHeight = baseFlameHeight + (int)setting;
        flamesGraphic.setOffset( flamesGraphic.getOffset().getX(),
                                 (int)Math.min( (float)flameHeight, 0 ) );
        iceGraphic.setOffset( (int)iceGraphic.getOffset().getX(),
                              (int)Math.min( (float)iceHeight, 0 ) );
    }
}
