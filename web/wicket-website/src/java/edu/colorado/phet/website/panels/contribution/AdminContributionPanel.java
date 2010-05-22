package edu.colorado.phet.website.panels.contribution;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.content.IndexPage;
import edu.colorado.phet.website.content.contribution.ContributionEditPage;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

public class AdminContributionPanel extends PhetPanel {

    private Model approvedLabelModel;
    private Model colorModel;
    private Model toggleModel;

    public AdminContributionPanel( String id, PageContext context, final Contribution contribution ) {
        super( id, context );

        setOutputMarkupId( true );

        add( HeaderContributor.forCss( CSS.CONTRIBUTION_MAIN ) );

        approvedLabelModel = new Model();
        colorModel = new Model();
        toggleModel = new Model();
        updateModels( contribution.isApproved() );

        Label approvedLabel = new Label( "approved", approvedLabelModel );
        approvedLabel.add( new AttributeAppender( "class", colorModel, " " ) );
        add( approvedLabel );

        add( ContributionEditPage.getLinker( contribution ).getLink( "admin-edit-link", context, getPhetCycle() ) );

        Link toggleLink = new AjaxFallbackLink( "toggle-approve-link" ) {
            public void onClick( AjaxRequestTarget target ) {
                final Boolean[] ret = new Boolean[1];
                boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        Contribution contrib = (Contribution) session.load( Contribution.class, contribution.getId() );
                        contrib.setApproved( !contrib.isApproved() );
                        ret[0] = contrib.isApproved();
                        session.update( contrib );
                        return true;
                    }
                } );
                if ( success ) {
                    updateModels( ret[0] );
                }
                target.addComponent( AdminContributionPanel.this );
            }
        };
        toggleLink.add( new Label( "toggle-approve-label", toggleModel ) );
        add( toggleLink );

        add( new Link( "delete-link" ) {
            public void onClick() {
                // TODO: do the delete magic
                boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        Contribution contrib = (Contribution) session.load( Contribution.class, contribution.getId() );
                        contrib.deleteMe( session );
                        return true;
                    }
                } );
                if ( success ) {
                    setResponsePage( IndexPage.class );
                }
            }
        } );

    }

    private void updateModels( boolean approved ) {
        approvedLabelModel.setObject( approved ? "approved" : "unapproved" );
        colorModel.setObject( approved ? "contribution-approved" : "contribution-unapproved" );
        toggleModel.setObject( approved ? "Unapprove" : "Approve" );
    }
}
