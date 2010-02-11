package edu.colorado.phet.website.panels;

import java.text.DateFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.hibernate.Session;

import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.content.SimulationPage;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.contribution.*;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.StringUtils;

/**
 * Translatable panel for showing a single contribution
 */
public class ContributionMainPanel extends PhetPanel {

    private String title;

    private static Logger logger = Logger.getLogger( ContributionMainPanel.class.getName() );

    public ContributionMainPanel( String id, final Contribution contribution, final PageContext context ) {
        super( id, context );

        add( new Label( "contribution-title", contribution.getTitle() ) );

        add( HeaderContributor.forCss( "/css/contribution-main-v1.css" ) );

        // TODO: localize
        title = "PhET contribution: " + contribution.getTitle();

        if ( contribution.isGoldStar() ) {
            // TODO: localize
            // TODO: link to legend?
            // TODO: add title
            add( new StaticImage( "gold-star-contribution", "/images/contributions/gold-star.jpg", "Gold Star Contribution" ) );
        }
        else {
            add( new InvisibleComponent( "gold-star-contribution" ) );
        }

        add( new ListView( "file", new LinkedList( contribution.getFiles() ) ) {
            protected void populateItem( ListItem item ) {
                ContributionFile file = (ContributionFile) item.getModel().getObject();
                Link link = file.getLinker().getLink( "file-link", context, getPhetCycle() );
                link.add( new Label( "file-name", file.getFilename() ) );
                item.add( link );

                item.add( new Label( "file-size", ( file.getSize() / 1000 ) + " kB" ) );
            }
        } );

        add( new Label( "authors", contribution.getAuthors() ) );
        add( new Label( "contact-email", contribution.getContactEmail() ) );
        add( new Label( "organization", contribution.getAuthorOrganization() ) );
        add( new Label( "title", contribution.getTitle() ) );
        add( new Label( "keywords", contribution.getKeywords() ) );
        add( new Label( "description", contribution.getDescription() ) );

        DateFormat format = DateFormat.getDateInstance( DateFormat.SHORT, getLocale() );

        add( new Label( "created", format.format( contribution.getDateCreated() ) ) );
        add( new Label( "updated", format.format( contribution.getDateUpdated() ) ) );

        if ( contribution.getSubjects().size() > 0 ) {
            add( new Label( "level", getLevelString( contribution ) ) );
        }
        else {
            add( new InvisibleComponent( "level" ) );
        }

        if ( contribution.getSubjects().size() > 0 ) {
            add( new Label( "type", getTypeString( contribution ) ) );
        }
        else {
            add( new InvisibleComponent( "type" ) );
        }

        if ( contribution.getSubjects().size() > 0 ) {
            add( new Label( "subject", getSubjectString( contribution ) ) );
        }
        else {
            add( new InvisibleComponent( "subject" ) );
        }

        if ( contribution.getDuration() != 0 ) {
            add( new LocalizedText( "duration", "contribution.duration", new Object[]{contribution.getDuration()} ) );
        }
        else {
            add( new InvisibleComponent( "duration" ) );
        }

        // simulation list
        final List<String> simStrings = new LinkedList<String>();
        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                for ( Object o : contribution.getSimulations() ) {
                    Simulation sim = (Simulation) o;
                    Simulation simulation = (Simulation) session.load( Simulation.class, sim.getId() );
                    LocalizedSimulation lsim = simulation.getBestLocalizedSimulation( getLocale() );
                    String simTitle = lsim.getTitle();
                    simStrings.add( "<a " + SimulationPage.getLinker( lsim ).getHref( context, getPhetCycle() ) + ">" + simTitle + "</a>" );
                }

                return true;
            }
        } );
        String str = StringUtils.combineStringsIntoList( this, simStrings, StringUtils.getSeparator( this ) );
        Label simLabel = new Label( "simulations", str );
        simLabel.setEscapeModelStrings( false );
        add( simLabel );


    }

    public String getTitle() {
        return title;
    }

    public String getLevelString( Contribution contribution ) {
        PhetLocalizer localizer = getPhetLocalizer();
        List<String> strings = new LinkedList<String>();
        for ( Object o : contribution.getLevels() ) {
            ContributionLevel level = (ContributionLevel) o;
            String key = level.getLevel().getTranslationKey();
            strings.add( localizer.getString( key, this ) );
        }
        return StringUtils.combineStringsIntoList( this, strings, StringUtils.getSeparator( this ) );
    }

    public String getTypeString( Contribution contribution ) {
        PhetLocalizer localizer = getPhetLocalizer();
        List<String> strings = new LinkedList<String>();
        for ( Object o : contribution.getTypes() ) {
            ContributionType type = (ContributionType) o;
            String key = type.getType().getTranslationKey();
            strings.add( localizer.getString( key, this ) );
        }
        return StringUtils.combineStringsIntoList( this, strings, StringUtils.getSeparator( this ) );
    }

    public String getSubjectString( Contribution contribution ) {
        PhetLocalizer localizer = getPhetLocalizer();
        List<String> strings = new LinkedList<String>();
        for ( Object o : contribution.getSubjects() ) {
            ContributionSubject subject = (ContributionSubject) o;
            String key = subject.getSubject().getTranslationKey();
            strings.add( localizer.getString( key, this ) );
        }
        return StringUtils.combineStringsIntoList( this, strings, StringUtils.getSeparator( this ) );
    }

}