// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.sponsorship;

import java.awt.Color;
import java.awt.Font;
import java.text.MessageFormat;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.InteractiveHTMLPane;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Panel that displays sponsor information.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SponsorPanel extends GridPanel {

    //TODO i18n
    private static final String SPONSORED_BY = "Sponsored by";
    private static final String SINCE_DATE = "(since {0})";

    public SponsorPanel( PhetApplicationConfig config ) {

        SponsorProperties properties = new SponsorProperties( config );
        assert ( properties.isWellFormed() );

        // property values, optional ones are null
        String imageResourceName = properties.getImageResourceName();
        String actualURL = properties.getActualURL();
        String visibleURL = properties.getVisibleURL();
        String sinceDate = properties.getSinceDate();

        // layout components, some of which are optional
        int xMargin = 40;
        int yMargin = 20;
        setBorder( new CompoundBorder( new LineBorder( Color.BLACK, 1 ), new EmptyBorder( yMargin, xMargin, yMargin, xMargin ) ) );
        setGridX( 0 ); // vertical
        setAnchor( Anchor.CENTER );
        add( new JLabel( SPONSORED_BY ) {{
            setFont( new PhetFont( 18 ) );
        }} );
        add( Box.createVerticalStrut( 15 ) );
        // image
        if ( imageResourceName != null ) {
            add( new JLabel( new ImageIcon( config.getResourceLoader().getImage( imageResourceName ) ) ) );
        }
        // url (both actual and visible required if you want any URL displayed)
        if ( actualURL != null && visibleURL != null ) {
            add( Box.createVerticalStrut( 15 ) );
            add( createInteractiveHTMLPane( actualURL, visibleURL, new PhetFont( 14 ) ) );
        }
        add( Box.createVerticalStrut( 15 ) );
        // since date
        if ( sinceDate != null ) {
            add( new JLabel( MessageFormat.format( SINCE_DATE, sinceDate ) ) {{
                setFont( new PhetFont( 10 ) );
            }} );
        }
    }

    private static InteractiveHTMLPane createInteractiveHTMLPane( String actualURL, String visibleURL, Font font ) {
        return new InteractiveHTMLPane( HTMLUtils.createStyledHTMLFromFragment( "<a href=\"" + actualURL + "\" target=\"_blank\">" + visibleURL, font ) ) {{
            setOpaque( false );
        }};
    }
}
