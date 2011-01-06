/* Copyright 2011, University of Colorado */

package edu.colorado.phet.common.phetcommon.application;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Window that displays translation credits for KSU ERCSME.
 * See #2624.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class KSUCreditsWindow extends JWindow {

    private static final String TRANSLATED_BY = PhetCommonResources.getString( "Common.About.CreditsDialog.TranslationCreditsTitle" );

    public KSUCreditsWindow( Frame parent ) {
        super( parent );

        JLabel label = new JLabel( TRANSLATED_BY );
        label.setFont( new PhetFont( 24 ) );

        JLabel logo =  new JLabel( new ImageIcon( PhetCommonResources.getImage( ( "logos/ERCSME-logo.png" ) ) ) );

        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        int margin = 18;
        panel.setBorder( new EmptyBorder( margin, margin, margin, margin ) );
        panel.add( label );
        panel.add( logo );

        setContentPane( panel );
        pack();

        // click on the window to make it go away
        addMouseListener( new MouseAdapter() {
            @Override
            public void mousePressed( MouseEvent event ) {
                dispose();
            }
        });
    }

    public static void main( String[] args ) {
        JWindow window = new KSUCreditsWindow( null );
        SwingUtils.centerWindowOnScreen( window );
        window.setVisible(  true );
    }
}
