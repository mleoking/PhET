package edu.colorado.phet.common.phetcommon.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import edu.colorado.phet.common.phetcommon.resources.DefaultResourceLoader;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.AnnotationParser;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * The Credits Dialog reads credits annotations for a single sim from <project>/data/<project>/credits.txt using the
 * AnnotationParser and displays it in the Phet About Dialog.  Here's an example content of a credits.txt file:
 * <p/>
 * phet-credits software-development=Chris Malley and Ron LeMaster design-team=Kathy Perkins, Carl Wieman, Wendy Adams, Danielle Harlow
 * <p/>
 * The keys in this file are "software-development" "design-team" "lead-design" and "interviews"; these are used as suffixes
 * on the keys in the phetcommon-strings.properties file to identify translatable strings such as:
 * Common.About.CreditsDialog.software-development=Software Development
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class CreditsDialog extends JDialog {
    // preferred size for the scrollpane, change this to affect initial dialog size
    private static final Dimension SCROLLPANE_SIZE = new Dimension( 525, 300 );

    private static final String TITLE = PhetCommonResources.getString( "Common.About.CreditsDialog.Title" );
    private static final String OK_BUTTON = PhetCommonResources.getString( "Common.About.OKButton" );
    private static final String PHET_DEV_TEAM = PhetCommonResources.getString( "Common.About.CreditsDialog.PhetDevelopmentTeam" );
    private static final String THIRD_PARTY_USAGE = PhetCommonResources.getString( "Common.About.CreditsDialog.UsesThirdPartySoftware" );
    private String projectName;
    private String phetLicenseString;

    public CreditsDialog( Dialog owner, String projectName ) {
        super( owner, TITLE, true );
        this.projectName = projectName;
        try {
            phetLicenseString = new DefaultResourceLoader().getResourceAsString( projectName + "/contrib-licenses/license-info.txt" );
        }
        catch( IOException e ) {
            //shouldn't happen for sims generated with the build process; license info is copied automatically.
            System.err.println( getClass().getName() + ": No license info found; Perhaps you need to generate license information for this simulation, using PhetBuildGUI->Misc->Generate License Info" );
//            e.printStackTrace();


        }
        String html = "<b>" + PHET_DEV_TEAM + "</b><br>\n" +
                      "<br>\n" +
                      getCreditsSnippet() +
                      "<br>\n" +
                      "<br>\n" +
                      "<b>" + THIRD_PARTY_USAGE + "</b><br>\n" +
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
            licenseText = HTMLUtils.createStyledHTMLFromFragment( licenseText );
            licenseText = licenseText.replaceAll( "\\n", "<br>" );
        }

        ContribLicenseDialog c = new ContribLicenseDialog( this, "License Details", licenseText );
        c.setVisible( true );
    }

    private String getLicenseSnippet() {
        if ( phetLicenseString == null ) {
            return "No license info found.";
        }
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
            if ( a[i].get( "license" ) != null ) {
                text += "<a href=\"http://" + id + "\">" + a[i].get( "license" ) + "<a>";
            }
            else {
                text += "license not found";
            }
            text+="<br><br>";
        }
        return text;

    }

    private String getCreditsSnippet() {
        String res = "";
        try {
            res = new DefaultResourceLoader().getResourceAsString( projectName + "/credits.txt" );
        }
        catch( IOException e ) {
            System.err.println( getClass().getName() + "Sim was missing credits information." );
//            e.printStackTrace();
        }
        if ( res.trim().length() == 0 ) {
            //all simulations should specify credits eventually
            return "No credits found.";
        }
        AnnotationParser.Annotation t = AnnotationParser.parse( res );
        HashMap map = t.getMap();
        ArrayList keys = t.getKeyOrdering();
        String credits = "";
        for ( Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            String value = (String) map.get( key );
            credits += translate( key, value ) + "<br>";
        }
        return credits;
    }

    private String translate( String key, String value ) {
        String pattern = PhetCommonResources.getString( "Common.About.CreditsDialog." + key );
        return pattern + ": " + value;
    }

    /**
     * Reads the license file from <project>/data/<project>/contib-licenses/<license>
     * where <license> is something like lgpl.txt or junit-cpl-v10.html.
     * <p/>
     * These files are placed in the contrib-licenses directory by the phet build process, see the Misc menu in the PhETBuildGUI
     *
     * @param id
     * @return
     */
    public String getLicenseText( String id ) {
        try {
            AnnotationParser.Annotation a = getAnnotation( id );
            return new DefaultResourceLoader().getResourceAsString( projectName + "/contrib-licenses/" + id + "-" + a.get( "licensefile" ) );
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        return "license not found";
    }

    private AnnotationParser.Annotation getAnnotation( String id ) {
        AnnotationParser.Annotation[] all = AnnotationParser.getAnnotations( phetLicenseString );
        AnnotationParser.Annotation a = null;
        for ( int i = 0; i < all.length; i++ ) {
            AnnotationParser.Annotation annotation = all[i];
            if ( annotation.getId().equals( id ) ) {
                a = annotation;
            }
        }
        return a;
    }

    public static void main( String[] args ) {
        //copy license info
        CreditsDialog dialog = new CreditsDialog( new JDialog(), "bound-states" );
        SwingUtils.centerWindowOnScreen( dialog );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }

            public void windowClosed( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
        dialog.setVisible( true );
    }
}