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

import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.data.contribution.*;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.StringUtils;

/**
 * Translatable panel for showing a single contribution
 */
public class ContributionMainPanel extends PhetPanel {

    private String title;

    private static Logger logger = Logger.getLogger( ContributionMainPanel.class.getName() );

    public ContributionMainPanel( String id, Contribution contribution, final PageContext context ) {
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

        add( new Label( "level", getLevelString( contribution ) ) );
        add( new Label( "type", getTypeString( contribution ) ) );
        add( new Label( "subject", getSubjectString( contribution ) ) );

    }

    public String getTitle() {
        return title;
    }

    public String getLevelString( Contribution contribution ) {
        PhetLocalizer localizer = getPhetLocalizer();
        String separator = localizer.getString( StringUtils.LIST_SEPARATOR_KEY, this );
        List<String> strings = new LinkedList<String>();
        for ( Object o : contribution.getLevels() ) {
            ContributionLevel level = (ContributionLevel) o;
            String key = level.getLevel().getAbbreviatedTranslationKey();
            strings.add( localizer.getString( key, this ) );
        }
        return StringUtils.combineStringsIntoList( this, strings, separator );
    }

    public String getTypeString( Contribution contribution ) {
        PhetLocalizer localizer = getPhetLocalizer();
        String separator = localizer.getString( StringUtils.LIST_SEPARATOR_KEY, this );
        List<String> strings = new LinkedList<String>();
        for ( Object o : contribution.getTypes() ) {
            ContributionType type = (ContributionType) o;
            String key = type.getType().getAbbreviatedTranslationKey();
            strings.add( localizer.getString( key, this ) );
        }
        return StringUtils.combineStringsIntoList( this, strings, separator );
    }

    public String getSubjectString( Contribution contribution ) {
        PhetLocalizer localizer = getPhetLocalizer();
        String separator = localizer.getString( StringUtils.LIST_SEPARATOR_KEY, this );
        List<String> strings = new LinkedList<String>();
        for ( Object o : contribution.getSubjects() ) {
            ContributionSubject subject = (ContributionSubject) o;
            String key = subject.getSubject().getAbbreviatedTranslationKey();
            strings.add( localizer.getString( key, this ) );
        }
        return StringUtils.combineStringsIntoList( this, strings, separator );
    }

}