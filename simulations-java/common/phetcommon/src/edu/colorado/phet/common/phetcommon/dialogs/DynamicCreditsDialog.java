package edu.colorado.phet.common.phetcommon.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import edu.colorado.phet.common.phetcommon.resources.DefaultResourceLoader;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

public class DynamicCreditsDialog extends JDialog {
    // preferred size for the scrollpane, change this to affect initial dialog size
    private static final Dimension SCROLLPANE_SIZE = new Dimension( 525, 300 );

    private static final String TITLE = PhetCommonResources.getString( "Common.About.CreditsDialog.Title" );
    private static final String OK_BUTTON = PhetCommonResources.getString( "Common.About.OKButton" );
    private String projectName;
    private String phetLicenseString;

    public DynamicCreditsDialog( Dialog owner, String projectName ) {
        super( owner, TITLE, true );
        this.projectName = projectName;
        try {
            phetLicenseString = new DefaultResourceLoader().getResourceAsString( projectName + "/contrib-licenses/license-info.txt" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        String html = "<b>PhET development team:</b><br>\n" +
                      "<br>\n" +
                      getCreditsSnippet() +
                      "<br>\n" +
                      "<br>\n" +
                      "<b>This program uses the following third-party software:</b><br>\n" +
                      "<br>\n" +
                      getLicenseSnippet();

        String phetLicenseHTML = HTMLUtils.createStyledHTMLFromFragment( html );
        InteractiveHTMLPane htmlPane = new InteractiveHTMLPane( phetLicenseHTML );
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
        htmlPane.setCaretPosition( 0 );
    }

    /**
     * An HTML editor pane that opens a web browser for hyperlinks.
     */
    public class InteractiveHTMLPane extends HTMLUtils.HTMLEditorPane {
        public InteractiveHTMLPane( String html ) {
            super( html );
            addHyperlinkListener( new HyperlinkListener() {
                public void hyperlinkUpdate( HyperlinkEvent e ) {
                    if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
                        displayLicenseForID( e.getURL().getHost() );
                    }
                }
            } );
        }

    }

    private void displayLicenseForID( String id ) {
        String licenseText = getLicenseText( id );
        if ( !licenseText.trim().startsWith( "<html" ) ) {
            licenseText = "<html>" + licenseText + "</html>";
            licenseText = licenseText.replaceAll( "\\n", "<br>" );
        }

        ContribLicenseDialog c = new ContribLicenseDialog( this, "License for " + id, licenseText );
        c.setVisible( true );
//        JOptionPane.showMessageDialog( this, licenseText );
    }

    private String getLicenseSnippet() {
        AnnotationParser.Annotation[] a = AnnotationParser.getAnnotations( phetLicenseString );
        String text = "";
        for ( int i = 0; i < a.length; i++ ) {
            String id = a[i].getId();
            String name = a[i].get( "name" );
            String description = a[i].get( "description" );
            String copyright = a[i].get( "copyright" );
            String website = a[i].get( "website" );
            text += name + ", " + description + "<br>";
            text += "&copy;&nbsp;" + copyright + " - " + website + "<br>";
            text += "<a href=\"http://" + id + "\">" + a[i].get( "license" ) + "<a><br><br>";
        }
        return text;

    }

    private String getCreditsSnippet() {
        String res = "";
        try {
            res = new DefaultResourceLoader().getResourceAsString( projectName + "/credits.txt" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        if ( res.trim().length() == 0 ) {
            res = "phet-credits team=PhET Interactive Simulations at the University of Colorado at Boulder";
        }
        AnnotationParser.Annotation t = AnnotationParser.parse( res );
        HashMap map = t.getMap();
        Set keys = map.keySet();
        String credits = "";
        for ( Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            String value = (String) map.get( key );
            credits += translate( key ) + ": " + value + "<br>";
        }
        return credits;
    }

    private String translate( String key ) {
        return PhetCommonResources.getString( "Common.About.CreditsDialog." + key );
    }

    public String getLicenseText( String id ) {
        try {
            AnnotationParser.Annotation[] all = AnnotationParser.getAnnotations( phetLicenseString );
            AnnotationParser.Annotation a = null;
            for ( int i = 0; i < all.length; i++ ) {
                AnnotationParser.Annotation annotation = all[i];
                if ( annotation.getId().equals( id ) ) {
                    a = annotation;
                }
            }
            return new DefaultResourceLoader().getResourceAsString( projectName + "/contrib-licenses/" + id + "-" + a.get( "licensefile" ) );
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        return "test license text for " + id;
    }

    public static void main( String[] args ) {
        //copy license info
        DynamicCreditsDialog dialog = new DynamicCreditsDialog( new JDialog(), "bound-states" );
        SwingUtils.centerWindowOnScreen( dialog );
        dialog.setVisible( true );
    }
}