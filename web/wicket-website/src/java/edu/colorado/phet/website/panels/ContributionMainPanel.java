package edu.colorado.phet.website.panels;

import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.data.contribution.ContributionFile;
import edu.colorado.phet.website.util.PageContext;

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

    }

    public String getTitle() {
        return title;
    }

}