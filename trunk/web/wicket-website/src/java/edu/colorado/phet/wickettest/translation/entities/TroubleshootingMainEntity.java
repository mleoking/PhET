package edu.colorado.phet.wickettest.translation.entities;

import edu.colorado.phet.wickettest.content.troubleshooting.TroubleshootingMainPanel;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.translation.PhetPanelFactory;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class TroubleshootingMainEntity extends TranslationEntity {

    public TroubleshootingMainEntity() {
        addString( "troubleshooting.main.title" );
        addString( "troubleshooting.main.intro" );
        addString( "troubleshooting.main.java" );
        addString( "troubleshooting.main.flash" );
        addString( "troubleshooting.main.javascript" );
        addString( "troubleshooting.main.faqs" );
        addString( "troubleshooting.top" );
        addString( "troubleshooting.main.q1.title" );
        addString( "troubleshooting.main.q1.answer" );
        addString( "troubleshooting.main.q2.title" );
        addString( "troubleshooting.main.q2.answer" );
        addString( "troubleshooting.main.q4.title" );
        addString( "troubleshooting.main.q4.answer" );
        addString( "troubleshooting.main.q5.title" );
        addString( "troubleshooting.main.q5.answer" );
        addString( "troubleshooting.main.q6.title" );
        addString( "troubleshooting.main.q6.answer" );
        addString( "troubleshooting.main.q7.title" );
        addString( "troubleshooting.main.q7.answer" );
        addString( "troubleshooting.main.q8.title" );
        addString( "troubleshooting.main.q8.answer" );
        addString( "troubleshooting.main.q9.title" );
        addString( "troubleshooting.main.q9.answer" );
        addString( "troubleshooting.main.q10.title" );
        addString( "troubleshooting.main.q10.answer" );
        addString( "troubleshooting.main.q11.title" );
        addString( "troubleshooting.main.q11.answer" );
        addString( "troubleshooting.main.q12.title" );
        addString( "troubleshooting.main.q12.answer" );
        addString( "troubleshooting.main.q13.title" );
        addString( "troubleshooting.main.q13.answer" );
        addString( "troubleshooting.main.licensingRequirements" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new TroubleshootingMainPanel( id, context );
            }
        }, "Troubleshooting (main)" );
    }

    public String getDisplayName() {
        return "Troubleshooting (main)";
    }
}
