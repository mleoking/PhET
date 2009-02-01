package edu.colorado.phet.common.phetcommon.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import edu.colorado.phet.common.phetcommon.resources.DefaultResourceLoader;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

public class DynamicCreditsDialog extends JDialog {
    // Resource (file) that contains the PhET license, in plain text format.
    private static final String LICENSE_INFO_RESOURCE = "contrib-licenses/license-info.txt";

    // preferred size for the scrollpane, change this to affect initial dialog size
    private static final Dimension SCROLLPANE_SIZE = new Dimension( 440, 300 );

    private static final String TITLE = "Additional Licenses";//todo: il8
    private static final String OK_BUTTON = PhetCommonResources.getString( "Common.About.OKButton" );
    private DialogLicenseDataSet dialogLicenseData;

    public DynamicCreditsDialog( Dialog owner ) {
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

    public DynamicCreditsDialog( Dialog owner, DialogLicenseDataSet dialogLicenseData ) {
        super( owner, TITLE, true /* modal */ );

        init( dialogLicenseData );
        this.dialogLicenseData = dialogLicenseData;
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
        String licenseText = dialogLicenseData.getLicenseText( id );
        JOptionPane.showMessageDialog( this, licenseText );
    }

    public void init( DialogLicenseDataSet dialogLicenseData ) {
        String phetLicenseHTML = HTMLUtils.setFontInStyledHTML( dialogLicenseData.getText(), new PhetFont() );
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
                String id = a[i].getId();
                String name=a[i].get( "name" );
                String description=a[i].get( "description" );
                String copyright=a[i].get( "copyright" );
                String website=a[i].get( "website" );
                text += name + ", " + description+"<br>";
                text+="&copy;&nbsp;"+copyright+" - "+website+"<br>";
                text+="<a href=\"http://" + id + "\">" + a[i].get( "license" ) + "<a><br><br>";
            }
            return text;
        }

        public int getCount() {
            return AnnotationParser.getAnnotations( text ).length;
        }

        public String getLicenseText( String id ) {
            try {
                AnnotationParser.Annotation[] all = AnnotationParser.getAnnotations( text );
                AnnotationParser.Annotation a=null;
                for ( int i = 0; i < all.length; i++ ) {
                    AnnotationParser.Annotation annotation = all[i];
                    if (annotation.getId().equals( id )){
                        a=annotation;
                    }
                }
                String t = new DefaultResourceLoader().getResourceAsString( "contrib-licenses/"+id+"-"+a.get("licensefile" ));
                return t;
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            return "test license text for " + id;
        }
    }

    public static void main( String[] args ) {
        //copy license info
        DynamicCreditsDialog dialog = new DynamicCreditsDialog( new JDialog() );
        SwingUtils.centerWindowOnScreen( dialog );
        dialog.setVisible( true );
    }
}