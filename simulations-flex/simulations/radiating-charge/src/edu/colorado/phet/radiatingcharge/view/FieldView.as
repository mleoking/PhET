/**
 * Created with IntelliJ IDEA.
 * User: Duso
 * Date: 6/6/12
 * Time: 8:25 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.radiatingcharge.view {
import edu.colorado.phet.radiatingcharge.model.FieldModel;

import flash.display.Sprite;

public class FieldView extends Sprite{

    private var myMainView: MainView;
    private var myFieldModel: FieldModel;

    public function FieldView( myMainView: MainView,  myFieldModel: FieldModel ) {
        this.myMainView = myMainView;
        this.myFieldModel = myFieldModel;
    }

    public function update():void{

    }//end of update()
}//end of class
}//end of package
