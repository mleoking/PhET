package edu.colorado.phet.wickettest.translation.entities;

import edu.colorado.phet.wickettest.content.troubleshooting.TroubleshootingJavascriptPanel;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.translation.PhetPanelFactory;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class TroubleshootingJavascriptEntity extends TranslationEntity {

    public TroubleshootingJavascriptEntity() {
        addString( "troubleshooting.javascript.intro" );
        addString( "troubleshooting.javascript.notJava" );
        addString( "troubleshooting.javascript.notify" );
        addString( "troubleshooting.javascript.faqs" );
        addString( "troubleshooting.javascript.q1.title" );
        addString( "troubleshooting.javascript.q1.yes" );
        addString( "troubleshooting.javascript.q1.no" );
        addString( "troubleshooting.javascript.q2.title" );
        addString( "troubleshooting.javascript.q2.answer" );
        addString( "troubleshooting.javascript.q3.title" );
        addString( "troubleshooting.javascript.q3.answer" );
        addString( "troubleshooting.javascript.q4.title" );
        addString( "troubleshooting.javascript.q4.answer" );
        addString( "troubleshooting.javascript.q5.title" );
        addString( "troubleshooting.javascript.q5.answer" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new TroubleshootingJavascriptPanel( id, context );
            }
        }, "Troubleshooting (javascript)" );
    }

    public String getDisplayName() {
        return "Troubleshooting (javascript)";
    }
}