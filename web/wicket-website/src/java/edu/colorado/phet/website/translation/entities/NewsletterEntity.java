package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.newsletter.ConfirmEmailSentPanel;
import edu.colorado.phet.website.newsletter.InitialSubscribePanel;
import edu.colorado.phet.website.newsletter.UnsubscribeLandingPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class NewsletterEntity extends TranslationEntity {
    public NewsletterEntity() {
        addString( "newsletter.subscribe.email" );
        addString( "newsletter.subscribe.submit" );
        addString( "newsletter.validation.email.Required" );
        addString( "newsletter.validation.email" );
        addString( "newsletter.validation.attempts" );
        addString( "newsletter.nowSubscribed" );
        addString( "newsletter.nowUnsubscribed" );
        addString( "newsletter.nowRegistered" );
        addString( "newsletter.toFinishRegistering" );
        addString( "newsletter.toFinishSubscribing" );
        addString( "newsletter.troubleshooting" );
        addString( "newsletter.awaitingConfirmation" );
        addString( "newsletter.confirmEmailSent.title" );
        addString( "newsletter.pastEditions" );
        addString( "newsletter.pleaseSignUp" );
        addString( "newsletter.subscribeTo" );
        addString( "newsletter.subscribe.title" );
        addString( "newsletter.confirmedEmail.title" );
        addString( "newsletter.confirmedEmail" );
        addString( "newsletter.unsubscribed.title" );
        addString( "newsletter.unsubscribed" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new InitialSubscribePanel( id, context );
            }
        }, "Initial subscribe page" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new ConfirmEmailSentPanel( id, context, new PhetUser() {{
                    setEmail( "email@example.com" );
                    setNewsletterOnlyAccount( true );
                }} );
            }
        }, "Sent confirmation: newsletter" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new ConfirmEmailSentPanel( id, context, new PhetUser() {{
                    setEmail( "email@example.com" );
                    setNewsletterOnlyAccount( false );
                }} );
            }
        }, "Sent confirmation: account" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new UnsubscribeLandingPanel( id, context, new PhetUser() {{
                    setEmail( "email@example.com" );
                    setNewsletterOnlyAccount( false );
                }} );
            }
        }, "Unsubscribed" );
    }

    public String getDisplayName() {
        return "Newsletter";
    }
}
