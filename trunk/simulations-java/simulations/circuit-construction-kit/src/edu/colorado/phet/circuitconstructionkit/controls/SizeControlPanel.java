package edu.colorado.phet.circuitconstructionkit.controls;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;

/**
 * User: Sam Reid
 * Date: Dec 16, 2006
 * Time: 11:03:19 AM
 */

public class SizeControlPanel extends VerticalLayoutPanel {
    private CCKModule module;

    public SizeControlPanel( CCKModule module ) {
        this.module = module;

        final JSpinner zoom = new JSpinner( new SpinnerNumberModel( 1, .1, 10, .1 ) );
        zoom.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Number value = (Number) zoom.getValue();
                double v = value.doubleValue();
                zoom( v );
            }
        } );
        zoom.setSize( 50, zoom.getPreferredSize().height );
        zoom.setPreferredSize( new Dimension( 50, zoom.getPreferredSize().height ) );

        ButtonGroup zoomGroup = new ButtonGroup();
        JRadioButton small = new JRadioButton( CCKResources.getString( "CCK3ControlPanel.SmallRadioButton" ) );
        small.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                zoom( 2 );
            }
        } );
        JRadioButton medium = new JRadioButton( CCKResources.getString( "CCK3ControlPanel.MediumRadioButton" ) );
        medium.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                zoom( 1 );
            }
        } );
        JRadioButton large = new JRadioButton( CCKResources.getString( "CCK3ControlPanel.LargeRadioButton" ) );
        large.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                zoom( .5 );
            }
        } );
        medium.setSelected( true );
        zoomGroup.add( large );
        zoomGroup.add( medium );
        zoomGroup.add( small );
        this.add( large );
        this.add( medium );
        this.add( small );
        setBorder( new CCKControlPanel.CCKTitledBorder( CCKResources.getString( "CCK3ControlPanel.SizePanelBorder" ) ) );

    }

    private void zoom( double scale ) {
        module.setZoom( scale );
    }
}
