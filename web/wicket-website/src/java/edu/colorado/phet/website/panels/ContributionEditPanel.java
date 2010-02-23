package edu.colorado.phet.website.panels;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IFormSubmittingComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.SimOrderItem;

public class ContributionEditPanel extends PhetPanel {

    private final Contribution contribution;
    private boolean creating;

    private PageContext context;

    private static Logger logger = Logger.getLogger( ContributionEditPanel.class.getName() );

    // variables for the simulation list
    private List<Simulation> simulations;
    private List<Simulation> allSimulations;
    private Map<Simulation, String> titleMap;
    private List<SimOrderItem> items;
    private List<SimOrderItem> allItems;

    public ContributionEditPanel( String id, PageContext context, Contribution preContribution ) {
        super( id, context );

        this.context = context;

        add( HeaderContributor.forCss( "/css/contribution-main-v1.css" ) );

        contribution = preContribution;

        creating = contribution.getId() == 0;

        //----------------------------------------------------------------------------
        // initialize the simulation list
        //----------------------------------------------------------------------------

        simulations = new LinkedList<Simulation>();
        allSimulations = new LinkedList<Simulation>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                if ( !creating ) {
                    Contribution activeContribution = (Contribution) session.load( Contribution.class, contribution.getId() );
                    for ( Object o : activeContribution.getSimulations() ) {
                        simulations.add( (Simulation) o );
                    }
                }
                List sims = session.createQuery( "select s from Simulation as s" ).list();
                for ( Object s : sims ) {
                    Simulation simulation = (Simulation) s;
                    if ( simulation.isVisible() ) {
                        allSimulations.add( simulation );
                    }
                }
                return true;
            }
        } );

        titleMap = new HashMap<Simulation, String>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                for ( Simulation simulation : allSimulations ) {
                    titleMap.put( simulation, simulation.getBestLocalizedSimulation( getLocale() ).getTitle() );
                }
                return true;
            }
        } );

        items = new LinkedList<SimOrderItem>();
        allItems = new LinkedList<SimOrderItem>();

        for ( Simulation simulation : simulations ) {
            items.add( new SimOrderItem( simulation, titleMap.get( simulation ) ) );
        }

        for ( Simulation simulation : allSimulations ) {
            allItems.add( new SimOrderItem( simulation, titleMap.get( simulation ) ) );
        }

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

            add( new SortedList<SimOrderItem>( "simulations", context, items, allItems ) {
                public boolean onAdd( final SimOrderItem item ) {
                    for ( SimOrderItem oldItem : items ) {
                        if ( oldItem.getId() == item.getId() ) {
                            // already in list. don't want duplicates!
                            return false;
                        }
                    }
                    return true;
                }

                public boolean onRemove( final SimOrderItem item, int index ) {
                    return true;
                }

                public Component getHeaderComponent( String id ) {
                    return new Label( id, "Simulations" );
                }
            } );
        }

        @Override
        protected void onSubmit() {
            super.onSubmit();

            logger.debug( "Main submission" );

            logger.debug( "authors: " + authorsText.getModelObjectAsString() );

            for ( SimOrderItem item : items ) {
                logger.debug( "simulation: " + item.getSimulation().getName() );
            }
        }

        @Override
        protected void delegateSubmit( IFormSubmittingComponent submittingComponent ) {
            // we must override the delegate submit because we will have child ajax buttons to handle adding / removing
            // things like the simulations. they should NOT trigger a full form submission.

            // yes this is ugly, maybe nested forms could be used?

            logger.debug( "submit delegation from: " + submittingComponent );

            if ( submittingComponent == null ) {
                // submit like normal
                super.delegateSubmit( submittingComponent );
            }
            else {
                submittingComponent.onSubmit();
            }
        }
    }


}
