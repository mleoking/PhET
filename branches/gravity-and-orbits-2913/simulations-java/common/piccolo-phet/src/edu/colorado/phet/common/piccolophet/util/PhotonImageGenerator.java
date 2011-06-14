// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * This application can be used to display and save PNG files for photons.
 *
 * @author Sam Reid / Chris Malley
 */
public class PhotonImageGenerator extends JFrame {

    private static final double MIN_DIAMETER = 5;
    private static final double MAX_DIAMETER = 200;
    private static final double DEFAULT_DIAMETER = 30;

    private static final double MIN_WAVELENGTH = VisibleColor.MIN_WAVELENGTH - 1; // include UV
    private static final double MAX_WAVELENGTH = VisibleColor.MAX_WAVELENGTH + 1; // include IR
    private static final double DEFAULT_WAVELENGTH = 600;

    private static final Color DEFAULT_BACKGROUND = Color.BLACK;

    private LinearValueControl diameterControl;
    private LinearValueControl wavelengthControl;
    private ColorControl backgroundControl;
    private PCanvas canvas;
    private PNode parentNode;

    public PhotonImageGenerator() {
        super( "Photon Image Generator" );

        diameterControl = new LinearValueControl( MIN_DIAMETER, MAX_DIAMETER, "diameter:", "##0", "pixels" );
        diameterControl.setValue( DEFAULT_DIAMETER );
        diameterControl.setUpDownArrowDelta( 1 );
        diameterControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateImagePreview();
            }
        } );

        wavelengthControl = new LinearValueControl( MIN_WAVELENGTH, MAX_WAVELENGTH, "wavelength:", "##0", "nm" );
        wavelengthControl.setValue( DEFAULT_WAVELENGTH );
        wavelengthControl.setUpDownArrowDelta( 1 );
        wavelengthControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateImagePreview();
            }
        } );

        backgroundControl = new ColorControl( this, "background:", DEFAULT_BACKGROUND, new Dimension( 30, 30 ) /* chipSize */ );
        backgroundControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                updateBackground();
            }
        } );

        canvas = new PCanvas();
        int canvasSize = (int) ( 1.2 * MAX_DIAMETER );
        canvas.setPreferredSize( new Dimension( canvasSize, canvasSize ) );
        parentNode = new PComposite();
        canvas.getLayer().addChild( parentNode );

        final JFrame thisFrame = this;
        JButton saveButton = new JButton( "Save..." );
        saveButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                // create the image
                double wavelength = wavelengthControl.getValue();
                double diameter = diameterControl.getValue();
                Image image = PhotonImageFactory.createPhotonImage( wavelength, diameter );

                // save the image to a file
                //TODO - verify that the filename ends with .png
                JFileChooser fc = new JFileChooser();
                fc.showSaveDialog( thisFrame ); // blocks until dialog is closed
                File outputFile = fc.getSelectedFile();
                if ( outputFile != null ) {
                    try {
                        ImageIO.write( BufferedImageUtils.toBufferedImage( image ), "PNG", outputFile );
                    }
                    catch ( IOException e1 ) {
                        e1.printStackTrace();
                    }
                }
            }
        } );

        JPanel controlPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( controlPanel );
        layout.setAnchor( GridBagConstraints.CENTER );
        controlPanel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( diameterControl, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addComponent( wavelengthControl, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addComponent( backgroundControl, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addComponent( saveButton, row++, column );

        JPanel contentPane = new JPanel();
        contentPane.add( canvas );
        contentPane.add( controlPanel );

        setContentPane( contentPane );
        pack();
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        updateImagePreview();
        updateBackground();
    }

    private void updateImagePreview() {
        double wavelength = wavelengthControl.getValue();
        double diameter = diameterControl.getValue();
        Image image = PhotonImageFactory.createPhotonImage( wavelength, diameter );
        PImage imageNode = new PImage( image );
        imageNode.setOffset( ( canvas.getWidth() - imageNode.getWidth() ) / 2, ( canvas.getHeight() - imageNode.getHeight() ) / 2 );
        parentNode.removeAllChildren();
        parentNode.addChild( imageNode );
    }

    private void updateBackground() {
        Color color = backgroundControl.getColor();
        canvas.setBackground( color );
    }

    public static void main( String[] args ) {
        JFrame frame = new PhotonImageGenerator();
        frame.show();
    }
}
