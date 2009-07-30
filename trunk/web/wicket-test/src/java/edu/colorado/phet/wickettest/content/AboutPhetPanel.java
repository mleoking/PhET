package edu.colorado.phet.wickettest.content;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.PageContext;

public class AboutPhetPanel extends PhetPanel {
    public AboutPhetPanel( String id, PageContext context ) {
        super( id, context );

        //add( new Label( "about-p1", new StringResourceModel( "about.p1", this, null, new String[]{"href=\"http://phet.colorado.edu/simulations/index.php\"", "href=\"http://phet.colorado.edu/research/index.php\""} ) ) );
        ResourceModel resourceModel = new ResourceModel( "about.p1" );
        System.out.println( resourceModel.getObject() );
        add( new Label( "about-p1", resourceModel ) );
    }
}
