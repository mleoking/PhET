package edu.colorado.phet.unfuddle.process;

import java.io.IOException;

/**
 * Created by: Sam
* May 14, 2008 at 7:30:54 PM
*/
public interface MyProcess {
    String invoke( String cmd ) throws IOException, InterruptedException;
}
