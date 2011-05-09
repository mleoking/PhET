package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.content.about.*;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class AboutPhetEntity extends TranslationEntity {
    public AboutPhetEntity() {
        addString( "about.title", "Title of the main About page" );

        addString( "about.p1", "The text surrounded with <a {0}> and </a> will be linked to the main PhET site, and the text surrounded with <a {1}> and </a> will be linked to the research page on the PhET site." );
        addString( "about.p2" );
        addString( "about.p3", "The text surrounded with <a {0}> and </a> will be linked to a page about PhET's rating system." );
        addString( "about.p4", "The text surrounded with <a {0}> and </a> will be linked to the main PhET site, and the text surrounded with <a {1}> and </a> will be linked to a page about Java technical support, and text surrounded with <a {2}> and </a> will be linked to a page about Flash technical support." );

        addString( "about.source-code.header" );
        addString( "about.source-code.location" );
        addString( "about.source-code.flash-simulations" );

        addString( "about.legend.header" );
        addString( "about.legend.guidance-recommended" );
        addString( "about.legend.under-construction" );
        addString( "about.legend.classroom-tested" );
        addString( "about.legend.gold-star" );

        addString( "tooltip.legend.guidanceRecommended" );
        addString( "tooltip.legend.underConstruction" );
        addString( "tooltip.legend.classroomTested" );
        addString( "tooltip.legend.goldStar" );
        addString( "tooltip.legend.phetDesigned" );

        addString( "about.contact.thePhetProject" );
        addString( "about.contact.licenseInformation" );
        addString( "about.contact.email" );
        addString( "about.contact.licensingText" );
        addString( "about.contact.correspondence" );

        addString( "about.licensing.options" );
        addString( "about.licensing.intro" );
        addString( "about.licensing.bothOptionsRequire" );
        addString( "about.licensing.phetInteractiveSimulations" );
        addString( "about.licensing.universityOfColorado" );
        addString( "about.licensing.letUsKnow" );
        addString( "about.licensing.taxDeductibleDonation" );
        addString( "about.licensing.contactPhet" );
        addString( "about.licensing.optionA" );
        addString( "about.licensing.gplSims" );
        addString( "about.licensing.ccaFulltext" );
        addString( "about.licensing.whatDoesThisMean" );
        addString( "about.licensing.cca.thisMeans" );
        addString( "about.licensing.cca.sourceOptionB" );
        addString( "about.licensing.optionB" );
        addString( "about.licensing.cca.intro" );
        addString( "about.licensing.gplIntro" );
        addString( "about.licensing.gpl.thisMeans" );
        addString( "about.licensing.gpl.fullText" );
        addString( "about.licensing.gpl.sourceCode" );
        addString( "about.licensing.alternativeLicenses" );
        addString( "about.licensing.alternativeLicenseOptions" );
        addString( "about.licensing.sourceCodeSection" );
        addString( "about.licensing.sourceCodeLink" );
        addString( "about.licensing.softwareAgreementSection" );
        addString( "about.licensing.softwareAgreementFullText" );

        addString( "about.contact.backRow" );
        addString( "about.contact.middleRow" );
        addString( "about.contact.frontRow" );
        addString( "about.contact.phetTeam" );

        addString( "about.news.title" );
        addString( "about.news.currentNewsletter" );
        addString( "about.news.pastNewsletters" );
        addString( "about.news.updates" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new AboutMainPanel( id, context );
            }
        }, "About (main)" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new AboutSourceCodePanel( id, context );
            }
        }, "About (source code)" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new AboutLegendPanel( id, context );
            }
        }, "About (legend)" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new AboutContactPanel( id, context );
            }
        }, "About (contact)" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new AboutLicensingPanel( id, context );
            }
        }, "About (licensing)" );
    }

    public String getDisplayName() {
        return "About";
    }
}
