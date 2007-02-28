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
import edu.colorado.phet.hydrogenatom.HADefaults;
import edu.colorado.phet.hydrogenatom.enums.DeBroglieView;
import edu.colorado.phet.hydrogenatom.module.HAModule;

/**
 * DeBroglieViewMenu is the "deBroglie View" submenu that appears in menu bar's Options menu.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeBroglieViewMenu extends JMenu {

    private HAModule _module;
    
    private JRadioButtonMenuItem _radialDistanceMenuItem;
    private JRadioButtonMenuItem _height3DMenuItem;
    private JRadioButtonMenuItem _brightnessMenuItem;
    
    public DeBroglieViewMenu( HAModule module ) {
        super( SimStrings.get( "menu.deBroglieView" ) );
        setMnemonic( SimStrings.getChar( "menu.deBroglieView.mnemonic" ) );

        _module = module;
        
        _radialDistanceMenuItem = new JRadioButtonMenuItem( SimStrings.get( "menu.deBroglieView.radialDistance" ) );
        _height3DMenuItem = new JRadioButtonMenuItem( SimStrings.get( "menu.deBroglieView.height3D" ) );
        _brightnessMenuItem = new JRadioButtonMenuItem( SimStrings.get( "menu.deBroglieView.brightness" ) );

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _radialDistanceMenuItem );
        buttonGroup.add( _height3DMenuItem );
        buttonGroup.add( _brightnessMenuItem );

        add( _radialDistanceMenuItem );
        add( _height3DMenuItem );
        add( _brightnessMenuItem );

        // Event handling
        ActionListener listener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handledeBroglieViewChange( e.getSource() );
            }
        };
        _radialDistanceMenuItem.addActionListener( listener );
        _height3DMenuItem.addActionListener( listener );
        _brightnessMenuItem.addActionListener( listener );

        // Default state
        if ( HADefaults.DEBROGLIE_VIEW == DeBroglieView.BRIGHTNESS ) {
            _brightnessMenuItem.setSelected( true );
        }
        else if ( HADefaults.DEBROGLIE_VIEW == DeBroglieView.RADIAL_DISTANCE ) {
            _radialDistanceMenuItem.setSelected( true );
        }
        else if ( HADefaults.DEBROGLIE_VIEW == DeBroglieView.HEIGHT_3D ) {
            _height3DMenuItem.setSelected( true );
        }
    }

    private void handledeBroglieViewChange( Object source ) {
        DeBroglieView view = null;
        if ( source == _brightnessMenuItem ) {
            view = DeBroglieView.BRIGHTNESS;
        }
        else if ( source == _radialDistanceMenuItem ) {
            view = DeBroglieView.RADIAL_DISTANCE;
        }
        else if ( source == _height3DMenuItem ) {
            view = DeBroglieView.HEIGHT_3D;
        }
        else {
            throw new UnsupportedOperationException( "unsupported event source" );
        }
        _module.setDeBroglieView( view );
    }
}
