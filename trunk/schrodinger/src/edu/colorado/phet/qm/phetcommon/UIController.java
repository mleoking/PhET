/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.VerticalLayoutPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 20, 2006
 * Time: 9:18:07 PM
 * Copyright (c) Feb 20, 2006 by Sam Reid
 */

public class UIController extends VerticalLayoutPanel {
    private PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();

    public UIController() {
//        AdvancedPanel advancedFBDPanel = new AdvancedPanel( "Font>>","Font<<");

        JFontChooser jFontChooser = new JFontChooser( null );
        jFontChooser.addListener( new JFontChooser.Listener() {
            public void fontChanged( Font font ) {
                phetLookAndFeel.setFont( font );
                apply();
            }
        } );
        addFullWidth( jFontChooser.getContentPane() );

        JFontChooser tabFont = new JFontChooser( null );
        tabFont.addListener( new JFontChooser.Listener() {
            public void fontChanged( Font font ) {
                phetLookAndFeel.setTabFont( font );
                apply();
            }
        } );
        addFullWidth( tabFont.getContentPane() );

        final JColorChooser jColorChooser = new JColorChooser();
        jColorChooser.getSelectionModel().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Color selected = jColorChooser.getSelectionModel().getSelectedColor();
                phetLookAndFeel.setBackgroundColor( selected );
                apply();
            }
        } );
        jColorChooser.setBorder( BorderFactory.createTitledBorder( "background" ) );
        addFullWidth( jColorChooser );

        final JColorChooser jColorChooser2 = new JColorChooser();
        jColorChooser2.getSelectionModel().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Color selected = jColorChooser2.getSelectionModel().getSelectedColor();
                phetLookAndFeel.setForegroundColor( selected );
                apply();
            }
        } );
        jColorChooser2.setBorder( BorderFactory.createTitledBorder( "foreground" ) );
        addFullWidth( jColorChooser2 );

    }

    private void apply() {
        phetLookAndFeel.apply();
        synchronizeUI();
    }

    private void synchronizeUI() {
        Frame[] frames = JFrame.getFrames();
        for( int i = 0; i < frames.length; i++ ) {
            Frame frame = frames[i];
            updateComponentTreeUI( frame );
        }
    }

    public static void updateComponentTreeUI( Component c ) {
        updateComponentTreeUI0( c );
        c.invalidate();
        c.validate();
        c.repaint();
    }

    private static void updateComponentTreeUI0( Component c ) {
        if( c instanceof UIController ) {
            return;
        }
        if( c instanceof JComponent ) {
            ( (JComponent)c ).updateUI();
        }
        Component[] children = null;
        if( c instanceof JMenu ) {
            children = ( (JMenu)c ).getMenuComponents();
        }
        else if( c instanceof Container ) {
            children = ( (Container)c ).getComponents();
        }
        if( children != null ) {
            for( int i = 0; i < children.length; i++ ) {
                updateComponentTreeUI0( children[i] );
            }
        }
    }

    public static void main( String[] args ) {
        showUIController();
    }

    public static void showUIController() {
        JFrame frame = new JFrame();
        frame.setContentPane( new JScrollPane( new UIController() ) );
        frame.pack();
        frame.setSize( frame.getSize().width, 700 );
        frame.setVisible( true );
    }

}
