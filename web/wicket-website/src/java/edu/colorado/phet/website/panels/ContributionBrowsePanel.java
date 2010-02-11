package edu.colorado.phet.website.panels;

import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.hibernate.Session;

import edu.colorado.phet.website.content.ContributionPage;
import edu.colorado.phet.website.content.SimulationPage;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.data.contribution.ContributionLevel;
import edu.colorado.phet.website.data.contribution.ContributionType;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

public class ContributionBrowsePanel extends PhetPanel {

    private final List<Contribution> newContributions;

    public ContributionBrowsePanel( String id, final PageContext context, final List<Contribution> contributions ) {
        super( id, context );

        newContributions = new LinkedList<Contribution>();

        // fill our new contributions list, and make sure to load all of the data that is lazy that we need
        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                for ( Contribution oldContrib : contributions ) {
                    Contribution contribution = (Contribution) session.load( Contribution.class, oldContrib.getId() );

                    // we need to read levels
                    contribution.getLevels();

                    // we also need to read the types
                    contribution.getTypes();

                    for ( Object o : contribution.getSimulations() ) {
                        Simulation sim = (Simulation) o;

                        // we need to be able to read these to determine the localized simulation title later
                        sim.getLocalizedSimulations();
                    }
                }
                return true;
            }
        } );

        // TODO: localize

        add( new ListView( "contributions", contributions ) {
            protected void populateItem( ListItem item ) {
                Contribution contribution = (Contribution) item.getModel().getObject();
                Link link = ContributionPage.createLink( "contribution-link", context, contribution );
                link.add( new Label( "contribution-title", contribution.getTitle() ) );
                item.add( link );
                item.add( new Label( "contribution-authors", contribution.getAuthors() ) );

                Label levelLabel = new Label( "contribution-level", getLevelString( contribution ) );
                levelLabel.setEscapeModelStrings( false );
                item.add( levelLabel );

                Label typeLabel = new Label( "contribution-type", getTypeString( contribution ) );
                typeLabel.setEscapeModelStrings( false );
                item.add( typeLabel );

                // get a sorted (for the locale) list of simulations
                List<LocalizedSimulation> lsims = new LinkedList<LocalizedSimulation>();
                for ( Object o : contribution.getSimulations() ) {
                    lsims.add( ( (Simulation) o ).getBestLocalizedSimulation( getLocale() ) );
                }
                HibernateUtils.orderSimulations( lsims, getLocale() );

                // add the list in
                item.add( new ListView( "contribution-simulations", lsims ) {
                    protected void populateItem( ListItem item ) {
                        LocalizedSimulation lsim = (LocalizedSimulation) item.getModel().getObject();
                        Link link = SimulationPage.createLink( "simulation-link", context, lsim );
                        link.add( new Label( "simulation-title", lsim.getTitle() ) );
                        item.add( link );
                    }
                } );

                // TODO: localize
                item.add( new Label( "contribution-updated", contribution.getDateUpdated().toString() ) );
            }
        } );

    }

    public String getLevelString( Contribution contribution ) {
        PhetLocalizer localizer = getPhetLocalizer();
        String ret = "";
        boolean started = false;
        for ( Object o : contribution.getLevels() ) {
            if ( started ) {
                ret += "<br/>";
            }
            started = true;

            ContributionLevel level = (ContributionLevel) o;
            String key = level.getLevel().getAbbreviatedTranslationKey();
            ret += localizer.getString( key, this );
        }
        return ret;
    }

    public String getTypeString( Contribution contribution ) {
        PhetLocalizer localizer = getPhetLocalizer();
        String ret = "";
        boolean started = false;
        for ( Object o : contribution.getTypes() ) {
            if ( started ) {
                ret += "<br/>";
            }
            started = true;

            ContributionType type = (ContributionType) o;
            String key = type.getType().getAbbreviatedTranslationKey();
            ret += localizer.getString( key, this );
        }
        return ret;
    }

    public String getSimulationString( Contribution contribution ) {
        PhetLocalizer localizer = getPhetLocalizer();
        String ret = "";
        boolean started = false;
        for ( Object o : contribution.getSimulations() ) {
            if ( started ) {
                ret += "<br/>";
            }
            started = true;

            Simulation sim = (Simulation) o;
            LocalizedSimulation lsim = sim.getBestLocalizedSimulation( getLocale() );

            // translated title
            ret += lsim.getTitle();
        }
        return ret;
    }

}