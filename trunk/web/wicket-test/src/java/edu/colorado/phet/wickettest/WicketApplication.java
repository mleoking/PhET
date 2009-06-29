package edu.colorado.phet.wickettest;

import org.apache.wicket.protocol.http.WebApplication;

public class WicketApplication extends WebApplication {
    public Class getHomePage() {
        return HelloWorld.class;
    }
}
