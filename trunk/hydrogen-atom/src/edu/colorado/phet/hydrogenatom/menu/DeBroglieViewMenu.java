/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.enums.DeBroglieView;

/**
 * DeBroglieViewMenu is the "deBroglie View" submenu that appears in menu bar's Options menu.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeBroglieViewMenu extends JMenu {

    private JRadioButtonMenuItem _brightnessMagnitudeMenuItem;
    private JRadioButtonMenuItem _brightnessMenuItem;
    private JRadioButtonMenuItem _radialDistanceMenuItem;
    private JRadioButtonMenuItem _height3DMenuItem;

    public DeBroglieViewMenu() {
        super( SimStrings.get( "menu.deBroglieView" ) );
        setMnemonic( SimStrings.getChar( "menu.deBroglieView.mnemonic" ) );

        _brightnessMagnitudeMenuItem = new JRadioButtonMenuItem( SimStrings.get( "menu.deBroglieView.brightnessMagnitude" ) );
        _brightnessMenuItem = new JRadioButtonMenuItem( SimStrings.get( "menu.deBroglieView.brightness" ) );
        _radialDistanceMenuItem = new JRadioButtonMenuItem( SimStrings.get( "menu.deBroglieView.radialDistance" ) );
        _height3DMenuItem = new JRadioButtonMenuItem( SimStrings.get( "menu.deBroglieView.height3D" ) );

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _brightnessMagnitudeMenuItem );
        buttonGroup.add( _brightnessMenuItem );
        buttonGroup.add( _radialDistanceMenuItem );
        buttonGroup.add( _height3DMenuItem );

        add( _brightnessMagnitudeMenuItem );
        add( _brightnessMenuItem );
        add( _radialDistanceMenuItem );
        add( _height3DMenuItem );

        // Event handling
        ActionListener listener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handledeBroglieViewChange( e.getSource() );
            }
        };
        _brightnessMagnitudeMenuItem.addActionListener( listener );
        _brightnessMenuItem.addActionListener( listener );
        _radialDistanceMenuItem.addActionListener( listener );
        _height3DMenuItem.addActionListener( listener );

        // Default state
        _brightnessMagnitudeMenuItem.setSelected( true );
    }

    private void handledeBroglieViewChange( Object source ) {
        DeBroglieView view = null;
        if ( source == _brightnessMagnitudeMenuItem ) {
            view = DeBroglieView.BRIGHTNESS_MAGNITUDE;
        }
        else if ( source == _brightnessMenuItem ) {
            view = DeBroglieView.BRIGHTNESS;
        }
        else if ( source == _radialDistanceMenuItem ) {
            view = DeBroglieView.RADIAL_DISTANCE;
        }
        else if ( source == _height3DMenuItem ) {
            view = DeBroglieView.HEIGHT_3D;
        }
        //XXX tell DeBroglieNode instance about view
    }
}
