package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.content.ResearchPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class ResearchEntity extends TranslationEntity {

    public ResearchEntity() {
//        addString( "research.intro" );
//        addString( "research.additional" );
//        addString( "research.immediateInterests" );
//        addString( "research.simsAffectStudents" );
//        addString( "research.simsAffectStudents.point1" );
//        addString( "research.simsAffectStudents.point2" );
//        addString( "research.simsAffectStudents.point3" );
//        addString( "research.simsEnvironments" );
//        addString( "research.simsEnvironments.point1" );
//        addString( "research.simsEnvironments.point2" );
//        addString( "research.simsEnvironments.point3" );
//        addString( "research.publicationsAndPresentations" );
//        addString( "research.aboutPhet" );
//        addString( "research.effectivenessStudies" );
//        addString( "research.studentBeliefs" );

        addString( "research.top" );
        addString( "research.understand.1" );
        addString( "research.understand.2" );
        addString( "research.understand.3" );
        addString( "research.intro" );
        addString( "research.commonQuestions" );
        addString( "research.question.1.title" );
        addString( "research.question.1.answer" );
        addString( "research.question.2.title" );
        addString( "research.question.2.answer" );
        addString( "research.question.3.title" );
        addString( "research.question.3.answer" );
        addString( "research.immediateInterests" );
        addString( "research.interest.analogy" );
        addString( "research.interest.norms" );
        addString( "research.interest.exploration" );
        addString( "research.interest.homework" );
        addString( "research.interest.chemistry" );
        addString( "research.publicationsAndPresentations" );
        addString( "research.publications.design" );
        addString( "research.publications.use" );
        addString( "research.publications.sims" );
        addString( "research.publications.perceptions" );

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
