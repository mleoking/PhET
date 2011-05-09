package edu.colorado.phet.website.newsletter;

import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.constants.WebsiteConstants;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HtmlUtils;
import edu.colorado.phet.website.util.PageContext;

public class ConfirmEmailSentPanel extends PhetPanel {
    public ConfirmEmailSentPanel( String id, PageContext context, PhetUser user ) {
        super( id, context );

        // show them the "newsletter" option if they are newsletter-only, OR if they are already registered and confirmed.
        if ( user.isNewsletterOnlyAccount() || user.isConfirmed() ) {
            add( new LocalizedText( "toFinish", "newsletter.toFinishSubscribing", new Object[] {
                    HtmlUtils.encode( user.getEmail() )
            } ) );
            add( new LocalizedText( "pleaseCheck", "newsletter.pleaseCheckSubscribing" ) );
        }
        else {
            add( new LocalizedText( "toFinish", "newsletter.toFinishRegistering", new Object[] {
                    HtmlUtils.encode( user.getEmail() )
            } ) );
            add( new LocalizedText( "pleaseCheck", "newsletter.pleaseCheckRegistering" ) );
        }

        add( new LocalizedText( "trouble", "newsletter.troubleshooting", new Object[] {
                "<a href=\"mailto:" + WebsiteConstants.HELP_EMAIL + "\">" + WebsiteConstants.HELP_EMAIL + "</a>" // TODO: factor out somewhere
        } ) );
    }
}
