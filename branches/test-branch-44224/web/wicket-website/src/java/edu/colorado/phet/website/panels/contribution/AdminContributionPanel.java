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

/**
 * A small control panel that is displayed above a contribution if a user is logged in as a PhET team member. Useful for
 * quick editing options for administrators.
 */
public class AdminContributionPanel extends PhetPanel {

    private Model<String> approvedLabelModel = new Model<String>();
    private Model<String> colorModel = new Model<String>();
    private Model<String> toggleApproveModel = new Model<String>();
    private Model<String> toggleGoldStarModel = new Model<String>();

    public AdminContributionPanel( String id, PageContext context, final Contribution contribution ) {
        super( id, context );

        setOutputMarkupId( true );

        add( HeaderContributor.forCss( CSS.CONTRIBUTION_MAIN ) );

        updateModels( contribution.isApproved(), contribution.isGoldStar() );

        Label approvedLabel = new Label( "approved", approvedLabelModel );
        approvedLabel.add( new AttributeAppender( "class", colorModel, " " ) );
        add( approvedLabel );

        add( ContributionEditPage.getLinker( contribution ).getLink( "admin-edit-link", context, getPhetCycle() ) );

        Link toggleApprovedLink = new AjaxFallbackLink( "toggle-approve-link" ) {
            public void onClick( AjaxRequestTarget target ) {
                final Boolean[] ret = new Boolean[2];
                boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        Contribution contrib = (Contribution) session.load( Contribution.class, contribution.getId() );
                        contrib.setApproved( !contrib.isApproved() );
                        ret[0] = contrib.isApproved();
                        ret[1] = contrib.isGoldStar();
                        session.update( contrib );
                        return true;
                    }
                } );
                if ( success ) {
                    updateModels( ret[0], ret[1] );
                }
                target.addComponent( AdminContributionPanel.this );
            }
        };
        toggleApprovedLink.add( new Label( "toggle-approve-label", toggleApproveModel ) );
        add( toggleApprovedLink );

        Link toggleGoldStarLink = new AjaxFallbackLink( "toggle-gold-star-link" ) {
            public void onClick( AjaxRequestTarget target ) {
                final Boolean[] ret = new Boolean[2];
                boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        Contribution contrib = (Contribution) session.load( Contribution.class, contribution.getId() );
                        contrib.setGoldStar( !contrib.isGoldStar() );
                        ret[0] = contrib.isApproved();
                        ret[1] = contrib.isGoldStar();
                        session.update( contrib );
                        return true;
                    }
                } );
                if ( success ) {
                    updateModels( ret[0], ret[1] );
                }
                target.addComponent( AdminContributionPanel.this );
            }
        };
        toggleGoldStarLink.add( new Label( "toggle-gold-star-label", toggleGoldStarModel ) );
        add( toggleGoldStarLink );

        add( new Link( "delete-link" ) {
            public void onClick() {
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

    private void updateModels( boolean approved, boolean goldStar ) {
        approvedLabelModel.setObject( approved ? "approved" : "unapproved" );
        colorModel.setObject( approved ? "contribution-approved" : "contribution-unapproved" );
        toggleApproveModel.setObject( approved ? "Unapprove" : "Approve" );
        toggleGoldStarModel.setObject( goldStar ? "Remove gold star" : "Add gold star" + " (reload to see effect)" );
    }
}
