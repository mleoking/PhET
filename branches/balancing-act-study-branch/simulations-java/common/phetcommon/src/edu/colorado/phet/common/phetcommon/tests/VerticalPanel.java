package edu.colorado.phet.common.phetcommon.tests;

/**
 * In IntelliJ IDEA 10.5.1 #IU-107.322 the "addChild" in VerticalPanel's super call is underlined in red,
 * with the mouseover message "Cannot reference "Component.addChild" before supertype constructor has been called"
 */
//Parent class with an overrideable method
class Component {
    void addChild( Component b ) {
    }
}

class Panel extends Component {
    public Panel( Component pNode ) {
    }

    Panel() {
    }
}

public class VerticalPanel extends Panel {
    public VerticalPanel() {
        super( new Component() {{
            addChild( new Panel() );
        }} );
    }

    public static void main( String[] args ) {
        System.out.println( "new SelectedItemNode() = " + new VerticalPanel() );
    }
}