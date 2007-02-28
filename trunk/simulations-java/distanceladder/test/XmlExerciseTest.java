
import edu.colorado.games4education.exercise.ExerciseView;
import edu.colorado.games4education.exercise.MessageView;
import edu.colorado.games4education.exercise.XmlExercise;
import edu.colorado.games4education.exercise.XmlMessage;

/**
 * Class: XmlExerciseTest
 * Class: PACKAGE_NAME
 * User: Ron LeMaster
 * Date: Mar 20, 2004
 * Time: 9:35:39 PM
 */

public class XmlExerciseTest {

    public static void main( String[] args ) {
        XmlExercise xe = new XmlExercise( "exercises/vogon-question-1.xml" );
        ExerciseView ev = new ExerciseView( xe );
        ev.doIt();

        XmlMessage xm = new XmlMessage( "exercises/intro.xml" );
        MessageView mv = new MessageView( xm );
        mv.doIt();
    }
}
