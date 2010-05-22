package edu.colorado.phet.website.panels.contribution;

import java.text.DateFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.components.RawLabel;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.constants.Images;
import edu.colorado.phet.website.content.contribution.AddContributionCommentPage;
import edu.colorado.phet.website.content.contribution.ContributionGuidelinesPanel;
import edu.colorado.phet.website.content.contribution.ContributionPage;
import edu.colorado.phet.website.content.contribution.NominateContributionPage;
import edu.colorado.phet.website.content.search.SearchResultsPage;
import edu.colorado.phet.website.content.simulations.SimulationPage;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.contribution.*;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.*;

/**
 * Panel for showing a single contribution (most information about it), and then comments below
 */
public class ContributionMainPanel extends PhetPanel {

    private String title;

    private static final Logger logger = Logger.getLogger( ContributionMainPanel.class.getName() );

    public ContributionMainPanel( String id, final Contribution contribution, final PageContext context ) {
        super( id, context );

        add( new Label( "contribution-title", contribution.getTitle() ) );

        add( HeaderContributor.forCss( CSS.CONTRIBUTION_MAIN ) );

        final PhetUser user = PhetSession.get().getUser();

        if ( user != null && user.isTeamMember() ) {
            add( new AdminContributionPanel( "admin-contrib-panel", context, contribution ) );
        }
        else {
            add( new InvisibleComponent( "admin-contrib-panel" ) );
        }

        // TODO: localize
        title = "PhET contribution: " + HtmlUtils.encode( contribution.getTitle() );

        if ( contribution.isGoldStar() ) {
            // TODO: localize
            // TODO: link to legend?
            // TODO: add title
            add( new StaticImage( "gold-star-contribution", Images.GOLD_STAR, "Gold Star Contribution" ) );
        }
        else {
            add( new InvisibleComponent( "gold-star-contribution" ) );
        }

        List<ContributionFile> files = new LinkedList<ContributionFile>();
        for ( Object o : contribution.getFiles() ) {
            files.add( (ContributionFile) o );
        }
        ContributionFile.orderFiles( files );

        add( new ListView( "file", files ) {
            protected void populateItem( ListItem item ) {
                ContributionFile file = (ContributionFile) item.getModel().getObject();
                Link link = file.getLinker().getLink( "file-link", context, getPhetCycle() );
                link.add( new Label( "file-name", file.getFilename() ) );
                item.add( link );

                item.add( new Label( "file-size", ( file.getSize() / 1000 ) + " kB" ) );
            }
        } );

        add( new LocalizedText( "zip-link", "contribution.view.zipDownload", new Object[]{
                "href=\"" + contribution.getZipLocation() + "\""
        } ) );

        add( new Label( "authors", contribution.getAuthors() ) );
        add( new Label( "contact-email", contribution.getContactEmail() ) );
        add( new Label( "organization", contribution.getAuthorOrganization() ) );
        add( new Label( "title", contribution.getTitle() ) );
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
            add( new LocalizedText( "duration", "contribution.duration", new Object[]{
                    contribution.getDuration()
            } ) );
        }
        else {
            add( new InvisibleComponent( "duration" ) );
        }

        initSimulationList( contribution, context );
        initKeywordList( contribution, context );

        add( new LocalizedText( "answers", contribution.isAnswersIncluded() ? "contribution.answers.yes" : "contribution.answers.no" ) );
        add( new Label( "language", StringUtils.getLocaleTitle( contribution.getLocale(), getLocale(), getPhetLocalizer() ) ) );

        if ( contribution.hasStandards() ) {
            add( new WebComponent( "standards" ) );

        }
        else {
            add( new InvisibleComponent( "standards" ) );
        }

        handleCheck( "stdK4A", contribution.isStandardK4A() );
        handleCheck( "std58A", contribution.isStandard58A() );
        handleCheck( "std912A", contribution.isStandard912A() );
        handleCheck( "stdK4B", contribution.isStandardK4B() );
        handleCheck( "std58B", contribution.isStandard58B() );
        handleCheck( "std912B", contribution.isStandard912B() );
        handleCheck( "stdK4C", contribution.isStandardK4C() );
        handleCheck( "std58C", contribution.isStandard58C() );
        handleCheck( "std912C", contribution.isStandard912C() );
        handleCheck( "stdK4D", contribution.isStandardK4D() );
        handleCheck( "std58D", contribution.isStandard58D() );
        handleCheck( "std912D", contribution.isStandard912D() );
        handleCheck( "stdK4E", contribution.isStandardK4E() );
        handleCheck( "std58E", contribution.isStandard58E() );
        handleCheck( "std912E", contribution.isStandard912E() );
        handleCheck( "stdK4F", contribution.isStandardK4F() );
        handleCheck( "std58F", contribution.isStandard58F() );
        handleCheck( "std912F", contribution.isStandard912F() );
        handleCheck( "stdK4G", contribution.isStandardK4G() );
        handleCheck( "std58G", contribution.isStandard58G() );
        handleCheck( "std912G", contribution.isStandard912G() );

        final List<ContributionComment> comments = new LinkedList<ContributionComment>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                List list = session.createQuery( "select cc from ContributionComment as cc where cc.contribution = :contribution order by cc.dateCreated " )
                        .setEntity( "contribution", contribution ).list();
                for ( Object o : list ) {
                    ContributionComment comment = (ContributionComment) o;

                    // load the user
                    comment.getPhetUser();
                    comments.add( comment );
                }
                return true;
            }
        } );

        if ( !comments.isEmpty() ) {

            final DateFormat dateFormat = DateFormat.getDateInstance( DateFormat.SHORT, getMyLocale() );

            add( new ListView( "contribution-comment", comments ) {
                protected void populateItem( ListItem item ) {
                    final ContributionComment comment = (ContributionComment) item.getModel().getObject();
                    item.add( new Label( "text", comment.getText() ) );
                    item.add( new Label( "date", dateFormat.format( comment.getDateUpdated() ) ) );
                    item.add( new Label( "author", comment.getPhetUser().getName() ) );
                    if ( user != null && user.isTeamMember() ) {
                        item.add( new Link( "contribution-admin-delete" ) {
                            public void onClick() {
                                HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                                    public boolean run( Session session ) {
                                        ContributionComment comm = (ContributionComment) session.load( ContributionComment.class, comment.getId() );
                                        session.delete( comm );
                                        return true;
                                    }
                                } );
                                setResponsePage( new RedirectPage( ContributionPage.getLinker( contribution.getId() ).getRawUrl( context, getPhetCycle() ) ) );
                            }
                        } );
                    }
                    else {
                        item.add( new InvisibleComponent( "contribution-admin-delete" ) );
                    }
                }
            } );
        }
        else {
            add( new InvisibleComponent( "contribution-comment" ) );
        }

        // here we manually build up an HTML form so we can send it directly to our custom adding comment page
        WebMarkupContainer commentForm = new WebMarkupContainer( "add-comment-form" );
        add( commentForm );
        commentForm.add( new AttributeAppender( "action", new Model( AddContributionCommentPage.getBaseLinker().getRawUrl( context, getPhetCycle() ) ), "" ) );
        Label commentContrib = new Label( "contrib-id-holder", "" );
        commentForm.add( commentContrib );
        commentContrib.add( new AttributeAppender( "value", new Model( Integer.toString( contribution.getId() ) ), "" ) );

        WebMarkupContainer nominateForm = new WebMarkupContainer( "nominate-form" );
        add( nominateForm );
        nominateForm.add( new AttributeAppender( "action", new Model( NominateContributionPage.getBaseLinker().getRawUrl( context, getPhetCycle() ) ), "" ) );
        Label commentContrib2 = new Label( "contrib-id-holder", "" );
        nominateForm.add( commentContrib2 );
        commentContrib2.add( new AttributeAppender( "value", new Model( Integer.toString( contribution.getId() ) ), "" ) );

        add( new StaticImage( "gold-star-nominate", Images.GOLD_STAR, "Gold Star Contribution" ) );
        add( new LocalizedText( "contribution-nominate-text", "contribution.view.nominateText", new Object[]{
                ContributionGuidelinesPanel.getLinker().getHref( context, getPhetCycle() ),
                "href=\"/publications/activities-guide/contribution-guidelines.pdf\""
        } ) );
    }

    private void handleCheck( String id, boolean value ) {
        if ( value ) {
            // TODO: localize / add alt
            add( new StaticImage( id, "/images/checkmark.gif", "Standard supported" ) );
        }
        else {
            add( new InvisibleComponent( id ) );
        }
    }

    private void initSimulationList( final Contribution contribution, final PageContext context ) {
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
        add( new RawLabel( "simulations", str ) );
    }

    private void initKeywordList( Contribution contribution, PageContext context ) {
        //add( new Label( "keywords", contribution.getKeywords() ) );
        final List<String> keyStrings = new LinkedList<String>();
        for ( String keyword : contribution.getKeywords().split( "," ) ) {
            keyword = keyword.trim();
            keyStrings.add( "<a " + SearchResultsPage.getLinker( keyword ).getHref( context, getPhetCycle() ) + ">" + keyword + "</a>" );
        }
        String str = StringUtils.combineStringsIntoList( this, keyStrings, StringUtils.getSeparator( this ) );
        add( new RawLabel( "keywords", str ) );
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