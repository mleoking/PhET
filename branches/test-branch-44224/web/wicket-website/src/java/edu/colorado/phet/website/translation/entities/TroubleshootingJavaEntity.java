package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.content.troubleshooting.TroubleshootingJavaPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class TroubleshootingJavaEntity extends TranslationEntity {

    public TroubleshootingJavaEntity() {
        addString( "troubleshooting.java.intro" );
        addString( "troubleshooting.java.faqs" );
        addString( "troubleshooting.java.q1.title" );
        addString( "troubleshooting.java.q1.answer" );
        addString( "troubleshooting.java.q2.title" );
        addString( "troubleshooting.java.q2.answer" );
        addString( "troubleshooting.java.q3.title" );
        addString( "troubleshooting.java.q3.answer" );
        addString( "troubleshooting.java.q4.title" );
        addString( "troubleshooting.java.q4.answer" );
        addString( "troubleshooting.java.q5.title" );
        addString( "troubleshooting.java.q5.answer" );
        addString( "troubleshooting.java.q6.title" );
        addString( "troubleshooting.java.q6.answer" );
        addString( "troubleshooting.java.q7.title" );
        addString( "troubleshooting.java.q7.answer" );
        addString( "troubleshooting.java.q8.title" );
        addString( "troubleshooting.java.q8.answer" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new TroubleshootingJavaPanel( id, context );
            }
        }, "Troubleshooting (java)" );
    }

    public String getDisplayName() {
        return "Troubleshooting (java)";
    }
}