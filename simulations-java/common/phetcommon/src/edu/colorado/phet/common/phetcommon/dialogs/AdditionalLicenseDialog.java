package edu.colorado.phet.common.phetcommon.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.DefaultResourceLoader;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

public class AdditionalLicenseDialog extends JDialog {
    // Resource (file) that contains the PhET license, in plain text format.
    private static final String LICENSE_INFO_RESOURCE = "contrib-licenses/license-info.txt";

    // preferred size for the scrollpane, change this to affect initial dialog size
    private static final Dimension SCROLLPANE_SIZE = new Dimension( 440, 300 );

    private static final String TITLE = "Additional Licenses";//todo: il8
    private static final String OK_BUTTON = PhetCommonResources.getString( "Common.About.OKButton" );

    public AdditionalLicenseDialog( Dialog owner ) {
        this( owner, readDataFromResources() );
    }

    private static DialogLicenseDataSet readDataFromResources() {
        String phetLicenseString = "";
        try {
            phetLicenseString = new DefaultResourceLoader().getResourceAsString( LICENSE_INFO_RESOURCE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return new DialogLicenseDataSet( phetLicenseString );
    }

    public AdditionalLicenseDialog( Dialog owner, DialogLicenseDataSet dialogLicenseData ) {
        super( owner, TITLE, true /* modal */ );

        init( dialogLicenseData );
    }

    public void init( DialogLicenseDataSet dialogLicenseData ) {
        String phetLicenseHTML = HTMLUtils.setFontInStyledHTML( dialogLicenseData.getText(), new PhetFont() );
        HTMLUtils.InteractiveHTMLPane htmlPane = new HTMLUtils.InteractiveHTMLPane( phetLicenseHTML );
        JScrollPane scrollPane = new JScrollPane( htmlPane );
        scrollPane.setPreferredSize( SCROLLPANE_SIZE );

        // OK button
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton( OK_BUTTON );
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setVisible( false );
                dispose();
            }
        } );
        buttonPanel.add( okButton );

        // layout
        JPanel panel = new JPanel( new BorderLayout() );
        panel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        panel.add( scrollPane, BorderLayout.CENTER );
        panel.add( buttonPanel, BorderLayout.SOUTH );
        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    private static class DialogLicenseDataSet {
        private String text;

        public DialogLicenseDataSet( String text ) {
            this.text = text;

        }

        public String getText() {
            AnnotationParser.Annotation[] a = AnnotationParser.getAnnotations( text );
            String text = "";
            for ( int i = 0; i < a.length; i++ ) {
                String o = a[i].get( "license" );
                text += a[i].getId() + ": " + o + "<br><br>";
            }
            return text;
        }

        public int getCount() {
            return AnnotationParser.getAnnotations( text ).length;
        }
    }

    public static void main( String[] args ) {
        AdditionalLicenseDialog dialog = new AdditionalLicenseDialog( new JDialog(), new DialogLicenseDataSet( "#This file identifies licenses of contibuted libraries\n" +
                                                                                                               "jfreechart license=LGPL licensefile=licence-LGPL.txt notes=lib/ contains several JARs not used by phet, jcommon.jar and jfreechart.jar have license as indicated\n" +
                                                                                                               "piccolo2d license=Piccolo2D License licensefile=license-piccolo.txt\n" +
                                                                                                               "schmidt-lee license=GPL description=This PhET project contains a modified version of two files Copyright (C) 1998 Kevin E. Schmidt and Michael A. Lee, under the GPL license." ) );
        SwingUtils.centerWindowOnScreen( dialog );
        dialog.setVisible( true );
    }
}