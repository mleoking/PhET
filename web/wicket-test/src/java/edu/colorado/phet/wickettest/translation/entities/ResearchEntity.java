package edu.colorado.phet.wickettest.translation.entities;

import edu.colorado.phet.wickettest.content.ResearchPanel;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.translation.PhetPanelFactory;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class ResearchEntity extends TranslationEntity {

    public ResearchEntity() {
        addString( "research.intro" );
        addString( "research.additional" );
        addString( "research.immediateInterests" );
        addString( "research.simsAffectStudents" );
        addString( "research.simsAffectStudents.point1" );
        addString( "research.simsAffectStudents.point2" );
        addString( "research.simsAffectStudents.point3" );
        addString( "research.simsEnvironments" );
        addString( "research.simsEnvironments.point1" );
        addString( "research.simsEnvironments.point2" );
        addString( "research.simsEnvironments.point3" );
        addString( "research.publicationsAndPresentations" );
        addString( "research.aboutPhet" );
        addString( "research.effectivenessStudies" );
        addString( "research.studentBeliefs" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new ResearchPanel( id, context );
            }
        }, "Research page" );
    }

    public String getDisplayName() {
        return "Research";
    }
}
