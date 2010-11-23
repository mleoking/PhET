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

        if ( user.isNewsletterOnlyAccount() ) {
            add( new LocalizedText( "toFinish", "newsletter.toFinishSubscribing", new Object[] {
                    HtmlUtils.encode( user.getEmail() )
            } ) );
        }
        else {
            add( new LocalizedText( "toFinish", "newsletter.toFinishRegistering", new Object[] {
                    HtmlUtils.encode( user.getEmail() )
            } ) );
        }

        add( new LocalizedText( "trouble", "newsletter.troubleshooting", new Object[] {
                WebsiteConstants.HELP_EMAIL,
                "<a href=\"mailto:" + WebsiteConstants.HELP_EMAIL + "\">" + WebsiteConstants.HELP_EMAIL + "</a>" // TODO: factor out somewhere
        } ) );
    }
}
