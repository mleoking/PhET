/**
 * Class: RightMirrorReflectivityControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Apr 1, 2003
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.lasers.model.ResonatingCavity;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class RightMirrorReflectivityControlPanel extends JPanel implements SimpleObserver {

    private JSlider reflectivitySlider;
    private JTextField reflectivityTF;
    private ResonatingCavity cavity;

    /**
     * @param cavity
     */
    public RightMirrorReflectivityControlPanel( final ResonatingCavity cavity ) {

        if( cavity != null ) {
            cavity.addObserver( this );
        }
        this.cavity = cavity;

        JPanel controlPanel = new JPanel( new GridLayout( 1, 2 ) );
        controlPanel.setPreferredSize( new Dimension( 125, 70 ) );

        JPanel timeReadoutPanel = new JPanel( new BorderLayout() );
        reflectivityTF = new JTextField( 4 );
        reflectivityTF.setEditable( false );
        reflectivityTF.setHorizontalAlignment( JTextField.RIGHT );
        Font clockFont = reflectivityTF.getFont();
        reflectivityTF.setFont( new Font( clockFont.getName(),
                                          LaserConfig.CONTROL_FONT_STYLE,
                                          LaserConfig.CONTROL_FONT_SIZE ) );

        reflectivityTF.setText( Float.toString( 10 ) );

        reflectivitySlider = new JSlider( JSlider.VERTICAL,
                                          0,
                                          100,
                                          50 );

        reflectivitySlider.setPreferredSize( new Dimension( 20, 50 ) );
        reflectivitySlider.setPaintTicks( true );
        reflectivitySlider.setMajorTickSpacing( 10 );
        reflectivitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
//                updateReflectivity( ( (float)reflectivitySlider.getValue() ) / 100 );
                cavity.setReflectivity(( (float)reflectivitySlider.getValue() ) / 100  );
                reflectivityTF.setText( Float.toString( reflectivitySlider.getValue() ) );
            }
        } );

        timeReadoutPanel.add( reflectivityTF, BorderLayout.CENTER );
        controlPanel.add( timeReadoutPanel );
        controlPanel.add( reflectivitySlider );

        Border border = new TitledBorder( "Reflectivity" );
        controlPanel.setBorder( border );
        this.add( controlPanel );
    }

//    private void updateReflectivity( float reflectivity ) {
//        new SetCavityReflectivityCmd( reflectivity ).doIt();
//    }

    public void update() {
        int reflectivity = (int)( cavity.getReflectivity() * 100 );
        if( reflectivitySlider.getValue() != reflectivity ) {
            reflectivityTF.setText( Integer.toString( reflectivity ) );
            reflectivitySlider.setValue( reflectivity );

        }
    }
}
