/**
 * Class: RightMirrorReflectivityControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Apr 1, 2003
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.model.mirror.PartialMirror;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class RightMirrorReflectivityControlPanel extends JPanel implements SimpleObserver {

    private JSlider reflectivitySlider;
    private JTextField reflectivityTF;
    private PartialMirror mirror;
    //    private ResonatingCavity cavity;

    /**
     */
    public RightMirrorReflectivityControlPanel( final PartialMirror mirror ) {
        //    public RightMirrorReflectivityControlPanel( final ResonatingCavity cavity ) {

        this.mirror = mirror;
        mirror.addObserver( this );
        //        if( cavity != null ) {
        //            cavity.addObserver( this );
        //        }
        //        this.cavity = cavity;

        JPanel timeReadoutPanel = new JPanel( new BorderLayout() );
        reflectivityTF = new JTextField( 2 );
        reflectivityTF.setEditable( false );
        reflectivityTF.setHorizontalAlignment( JTextField.RIGHT );
        Font clockFont = reflectivityTF.getFont();
        reflectivityTF.setFont( new Font( clockFont.getName(),
                                          LaserConfig.CONTROL_FONT_STYLE,
                                          LaserConfig.CONTROL_FONT_SIZE ) );

        reflectivityTF.setText( Double.toString( 10 ) );

        reflectivitySlider = new JSlider( JSlider.VERTICAL,
                                          0,
                                          100,
                                          50 );

        reflectivitySlider.setPreferredSize( new Dimension( 20, 50 ) );
        reflectivitySlider.setPaintTicks( true );
        reflectivitySlider.setMajorTickSpacing( 10 );
        reflectivitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                //                updateReflectivity( ( (double)reflectivitySlider.getValue() ) / 100 );
                mirror.setReflectivity( ( (double)reflectivitySlider.getValue() ) / 100 );
                reflectivityTF.setText( Double.toString( reflectivitySlider.getValue() ) );
            }
        } );

        JPanel controlPanel = new JPanel( new GridBagLayout() );
        //        JPanel controlPanel = new JPanel( new GridLayout( 1, 2 ) );
        //        controlPanel.setPreferredSize( new Dimension( 125, 70 ) );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 5, 0, 5 ), 20, 0 );

        //        timeReadoutPanel.add( reflectivityTF, BorderLayout.CENTER );
        //        controlPanel.add( timeReadoutPanel );
        //        controlPanel.add( reflectivityTF, gbc);
        //        gbc.gridx++;
        //        controlPanel.add( reflectivitySlider,gbc );
        //
        //        Border border = new TitledBorder( "Reflectivity" );
        //        controlPanel.setBorder( border );
        //        this.add( controlPanel );
        this.setLayout( new GridBagLayout() );
        this.add( reflectivitySlider, gbc );
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add( reflectivityTF, gbc );

        Border border = new TitledBorder( SimStrings.get( "RightMirrorReflectivityControlPanel.BorderTitle" ) );
        this.setBorder( border );
        //        this.add( controlPanel );
    }

    //    private void updateReflectivity( float reflectivity ) {
    //        new SetCavityReflectivityCmd( reflectivity ).doIt();
    //    }

    public void update() {
        int reflectivity = (int)( mirror.getReflectivity() * 100 );
        //        int reflectivity = (int)( cavity.getReflectivity() * 100 );
        if( reflectivitySlider.getValue() != reflectivity ) {
            reflectivityTF.setText( Integer.toString( reflectivity ) );
            reflectivitySlider.setValue( reflectivity );

        }
    }
}
