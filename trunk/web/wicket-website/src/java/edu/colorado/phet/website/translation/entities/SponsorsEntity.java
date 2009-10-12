package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.content.about.AboutSponsorsPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.panels.SponsorsPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class SponsorsEntity extends TranslationEntity {
    public SponsorsEntity() {
        addString( "sponsors.principalSponsors" );
        addString( "sponsors.hewlett" );
        addString( "sponsors.nsf" );
        addString( "sponsors.ksu" );
        addString( "sponsors.otherSponsors" );

        addString( "sponsors.header" );
        addString( "sponsors.financialSupport" );
        addString( "sponsors.platinum" );
        addString( "sponsors.gold" );
        addString( "sponsors.bronze" );
        addString( "sponsors.technicalSupport" );
        addString( "sponsors.piccolo.desc" );
        addString( "sponsors.jfreechart.desc" );
        addString( "sponsors.jade.desc" );
        addString( "sponsors.sourceforge.desc" );
        addString( "sponsors.proguard.desc" );
        addString( "sponsors.jetbrains.desc" );
        addString( "sponsors.ej.desc" );
        addString( "sponsors.bitrock.desc" );
        addString( "sponsors.royalinteractive.desc" );
        addString( "sponsors.dynamicalSystems.desc" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new SponsorsPanel( id, context );
            }
        }, "Sponsors Panel" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new AboutSponsorsPanel( id, context );
            }
        }, "Sponsors Page" );
    }

    public String getDisplayName() {
        return "Sponsors";
    }
}
