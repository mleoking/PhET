package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import edu.colorado.phet.eatingandexercise.view.LabelNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Jun 24, 2008 at 11:48:26 AM
 */
public class AgeRangeMessage extends PNode {
    public AgeRangeMessage() {
        LabelNode nonDisruptiveMessage = new LabelNode( "<html>This simulation is based on data<br>from 20-60 year olds.<html>" );
        addChild( nonDisruptiveMessage );
    }
}
