// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.application.Module;

/**
 * Created by Sam on 10/18/13.
 */
public class Report {
    long startTime = 0;
    private long currentTime;
    private HashMap<Module, ModuleInfo> moduleMap = new HashMap<Module, ModuleInfo>();
    private ArrayList<Module> modules = new ArrayList<Module>();
    private Module module = null;

    public void update() {
        currentTime = System.currentTimeMillis();
    }

    @Override public String toString() {
        String a = "elapsed time: " + ( currentTime - startTime ) / 1000.0 + " sec" + "\n";
        String b = "";
        for ( Module m : modules ) {
            ModuleInfo info = moduleMap.get( m );
            b = b + m.getName() + ": " + info.toString() + "\n";
        }
        return a + b;
    }

    public void setActiveModule( Module module ) {
        if ( module != this.module ) {
            if ( this.module != null ) {
                moduleMap.get( this.module ).setRunning( false );
            }
            this.module = module;
            moduleMap.get( this.module ).setRunning( true );
        }
    }

    public void moduleAdded( Module module ) {
        if ( this.startTime == 0 ) {
            startTime = System.currentTimeMillis();
        }
        this.modules.add( module );
        this.moduleMap.put( module, new ModuleInfo( module ) );
    }
}
