package edu.colorado.phet.wickettest.panels;

import java.util.*;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.wickettest.content.SimulationPage;
import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.PageContext;

public class SimulationIndexPanel extends PhetPanel {
    private List<String> letters;

    public SimulationIndexPanel( String id, final PageContext context, List<LocalizedSimulation> simulations ) {
        super( id, context );

        final Map<String, List<LocalizedSimulation>> lettermap = new HashMap<String, List<LocalizedSimulation>>();
        letters = new LinkedList<String>();

        for ( LocalizedSimulation simulation : simulations ) {
            String name = simulation.getTitle();
            String chr = HibernateUtils.getLeadingSimCharacter( name, context.getLocale() );
            if ( lettermap.containsKey( chr ) ) {
                lettermap.get( chr ).add( simulation );
            }
            else {
                List<LocalizedSimulation> li = new LinkedList<LocalizedSimulation>();
                li.add( simulation );
                lettermap.put( chr, li );
                letters.add( chr );
            }
        }

        Collections.sort( letters, new Comparator<String>() {
            public int compare( String a, String b ) {
                return a.compareTo( b );
            }
        } );


        add( new ListView( "sim-group", letters ) {
            protected void populateItem( ListItem item ) {
                String letter = item.getModelObjectAsString();
                item.setOutputMarkupId( true );
                item.setMarkupId( letter );
                item.add( new Label( "letter", letter ) );
                item.add( new ListView( "sim-entry", lettermap.get( letter ) ) {
                    protected void populateItem( ListItem subItem ) {
                        LocalizedSimulation simulation = (LocalizedSimulation) subItem.getModel().getObject();
                        Link link = SimulationPage.createLink( "sim-link", context, simulation );
                        link.add( new Label( "sim-title", simulation.getTitle() ) );
                        subItem.add( link );
                    }
                } );
            }
        } );

        add( HeaderContributor.forCss( "/css/simulation-index-v1.css" ) );

    }

    public List<String> getLetters() {
        return letters;
    }
}