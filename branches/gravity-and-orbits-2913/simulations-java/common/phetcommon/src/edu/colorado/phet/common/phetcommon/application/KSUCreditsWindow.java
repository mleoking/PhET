// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.application;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

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
        label.setFont( new PhetFont( 18 ) );

        JLabel logo = new JLabel( new ImageIcon( PhetCommonResources.getImage( ( "logos/ECSME-KSU-logos.jpg" ) ) ) );

        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        int margin = 12;
        panel.setBorder( new CompoundBorder( new LineBorder( Color.BLACK, 1 ), new EmptyBorder( margin, margin, margin, margin ) ) );
        panel.add( label );
        panel.add( Box.createVerticalStrut( 5 ) );
        panel.add( logo );

        setContentPane( panel );
        pack();

        // click on the window to make it go away
        addMouseListener( new MouseAdapter() {
            @Override
            public void mousePressed( MouseEvent event ) {
                dispose();
            }
        } );
    }

    public static void main( String[] args ) {
        JWindow window = new KSUCreditsWindow( null );
        SwingUtils.centerWindowOnScreen( window );
        window.setVisible( true );
    }
}
