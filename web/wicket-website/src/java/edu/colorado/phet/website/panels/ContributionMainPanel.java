package edu.colorado.phet.website.panels;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;

import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.StaticImage;

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

        if( contribution.isGoldStar()) {
            // TODO: localize
            // TODO: link to legend?
            // TODO: add title
            add( new StaticImage("gold-star-contribution", "/images/contributions/gold-star.jpg", "Gold Star Contribution"));
        } else {
            add( new InvisibleComponent( "gold-star-contribution"));
        }

    }

    public String getTitle() {
        return title;
    }

}