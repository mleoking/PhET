package edu.colorado.phet.lookandfeels;

import com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 3:58:03 PM
 * Copyright (c) Feb 18, 2007 by Sam Reid
 */

public class LookAndFeelMenu extends JMenu {

    public LookAndFeelMenu() {
        super( "Look & Feel" );
        UIManager.installLookAndFeel( "Oyoaha Default", OyoahaLookAndFeel.class.getName() );
        setMnemonic( 'l' );
        UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
        for( int i = 0; i < infos.length; i++ ) {
            final UIManager.LookAndFeelInfo info = infos[i];

            JMenuItem jMenuItem = new JMenuItem( info.getName() );
            jMenuItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    try {
                        UIManager.setLookAndFeel( info.getClassName() );
                        refreshApp();
                    }
                    catch( ClassNotFoundException e1 ) {
                        e1.printStackTrace();
                    }
                    catch( InstantiationException e1 ) {
                        e1.printStackTrace();
                    }
                    catch( IllegalAccessException e1 ) {
                        e1.printStackTrace();
                    }
                    catch( UnsupportedLookAndFeelException e1 ) {
                        e1.printStackTrace();
                    }
                }
            } );
            add( jMenuItem );
        }
        add( new ThemePackMenuItem( "Daisy", "Oyoaha.themepack.14.04.05\\daisy2.zotm" ) );
        String[] names = new String[]{"anidaisy matrix.zotm", "anidaisy.zotm", "daisy2.zotm", "flat1.zotm", "flat2.zotm", "flat3.zotm", "flat4.zotm", "flat5.zotm", "flat6.zotm", "flat7.zotm", "flat8.zotm", "gang.zotm", "gradient.zotm", "licence.txt", "slushy.zotm", "slushy10.zotm", "slushy2.zotm", "slushy3.zotm", "slushy4.zotm", "slushy5.zotm", "slushy6.zotm", "slushy7.zotm", "slushy8.zotm", "slushy9.zotm", "tfiberr.zotm", "tfiberr2.zotm", "tflat1.zotm", "tflat2.zotm", "tflat3.zotm", "tflat4.zotm", "tflat5.zotm", "tflat6.zotm", "tflat7.zotm", "tgang.zotm", "tgang2.zotm", "tgang3.zotm", "tzipper.zotm", "zipper.zotm"};
        for( int i = 0; i < names.length; i++ ) {
            add( new ThemePackMenuItem( names[i], "Oyoaha.themepack.14.04.05\\" + names[i] ) );
        }
    }

    public class ThemePackMenuItem extends JMenuItem {
        public ThemePackMenuItem( String res ) {
            this( res, res );
        }

        public ThemePackMenuItem( String name, final String resource ) {
            super( name );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    OyoahaLookAndFeel look = new OyoahaLookAndFeel();
                    URL url = getClass().getClassLoader().getResource( resource );
                    look.setOyoahaTheme( url );

                    try {
                        UIManager.setLookAndFeel( look );
                    }
                    catch( UnsupportedLookAndFeelException e1 ) {
                        e1.printStackTrace();
                    }
                    refreshApp();
                }
            } );
        }
    }

    /**
     * Makes sure the component tree UI of all frames are updated.
     * Taken from http://mindprod.com/jgloss/laf.html
     */
    private void refreshApp() {
// refreshing the Look and Feel of the entire app
        Frame frames[] = Frame.getFrames();

// refresh all Frames in the app
        for( int i = 0; i < frames.length; i++ ) {
            SwingUtilities.updateComponentTreeUI( frames[i] );
            Window windows[] = frames[i].getOwnedWindows();

            // refresh all windows and dialogs of the frame
            for( int j = 0; j < windows.length; j++ ) {
                SwingUtilities.updateComponentTreeUI( windows[j] );
            }
        }
// It should not be necessary to revalidate or repaint on top of that.
    }
}
