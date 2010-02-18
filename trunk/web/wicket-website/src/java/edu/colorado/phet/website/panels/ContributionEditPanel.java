package edu.colorado.phet.website.panels;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;

import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.util.PageContext;

public class ContributionEditPanel extends PhetPanel {

    private final Contribution contribution;
    private boolean creating;

    private static Logger logger = Logger.getLogger( ContributionEditPanel.class.getName() );

    public ContributionEditPanel( String id, PageContext context, Contribution preContribution ) {
        super( id, context );

        add( HeaderContributor.forCss( "/css/contribution-main-v1.css" ) );

        contribution = preContribution;

        creating = contribution.getId() == 0;

        add( new ContributionForm( "contributionform" ) );

        add( new FeedbackPanel( "feedback" ) );
    }

    public final class ContributionForm extends Form {

        private RequiredTextField authorsText;
        private RequiredTextField organizationText;
        private RequiredTextField emailText;
        private RequiredTextField titleText;
        private RequiredTextField keywordsText;

        public ContributionForm( String id ) {
            super( id );

            authorsText = new RequiredTextField( "contribution.edit.authors", new Model( creating ? "" : contribution.getAuthors() ) );
            add( authorsText );

            organizationText = new RequiredTextField( "contribution.edit.organization", new Model( creating ? "" : contribution.getAuthorOrganization() ) );
            add( organizationText );

            emailText = new RequiredTextField( "contribution.edit.email", new Model( creating ? "" : contribution.getContactEmail() ) );
            add( emailText );

            titleText = new RequiredTextField( "contribution.edit.title", new Model( creating ? "" : contribution.getTitle() ) );
            add( titleText );

            keywordsText = new RequiredTextField( "contribution.edit.keywords", new Model( creating ? "" : contribution.getKeywords() ) );
            add( keywordsText );
        }

        @Override
        protected void onSubmit() {
            super.onSubmit();

            logger.debug( "authors: " + authorsText.getModelObjectAsString() );
        }
    }
}
