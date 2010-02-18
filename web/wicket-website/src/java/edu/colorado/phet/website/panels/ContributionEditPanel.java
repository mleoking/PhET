package edu.colorado.phet.website.panels;

import org.apache.log4j.Logger;
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

        contribution = preContribution;

        creating = contribution.getId() == 0;

        add( new ContributionForm( "contributionform" ) );

        add( new FeedbackPanel( "feedback" ) );
    }

    public final class ContributionForm extends Form {

        private RequiredTextField authorsText;

        public ContributionForm( String id ) {
            super( id );

            authorsText = new RequiredTextField( "contribution.edit.authors", new Model( creating ? "" : contribution.getAuthors() ) );
            add( authorsText );
        }

        @Override
        protected void onSubmit() {
            super.onSubmit();

            logger.debug( "authors: " + authorsText.getModelObjectAsString() );
        }
    }
}
