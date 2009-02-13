package edu.colorado.phet.common.phetcommon.application;

/**
 * Encapsulation of the "Software Use Agreement".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SoftwareAgreement {
    
    private static SoftwareAgreement instance;
    
    // singleton
    private SoftwareAgreement() {}
    
    public static SoftwareAgreement getInstance() {
        if ( instance == null ) {
            instance = new SoftwareAgreement();
        }
        return instance;
    }

    /**
     * Version numbers are monotonically increasing.
     * @return
     */
    public int getVersion() {
        return 1; //TODO this should be read from the agreement file (property version= ?)
    }
    
    /**
     * Content of the agreement is String that is in HTML format.
     * @return
     */
    public String getContent() {
        // TODO this should be read from the agreement file (property content= ?)
        return "<html>" +
        "<b>PhET Software Agreement</b><br> " +
        "<br>" +
        "This agreement contains:<br>" +
        "<ul>" +
        "<li>License" +
        "<li>Privacy" +
        "<li>Disclaimers" +
        "</ul>" +
        "<br>" +
        "<b>License</b><br>" +
        "<br>" +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "<br>" +
        "<br>" +
        "<b>Privacy</b><br>" +
        "<br>" +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "<br>" +
        "<br>" +
        "<b>Disclaimers</b><br>" +
        "<br>" +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "blah blah blah blah blah blah blah blah blah blah blah blah blah blah blah<br> " +
        "<br>" +
        "</html>";
    }
}
