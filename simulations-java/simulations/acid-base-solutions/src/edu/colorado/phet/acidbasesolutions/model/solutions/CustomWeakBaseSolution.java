package edu.colorado.phet.acidbasesolutions.model.solutions;

import edu.colorado.phet.acidbasesolutions.model.bases.CustomWeakBase;
import edu.colorado.phet.acidbasesolutions.model.bases.CustomWeakBase.CustomWeakBaseListener;


public class CustomWeakBaseSolution extends WeakBaseSolution {
    
    private final CustomWeakBase _base;
    private final CustomWeakBaseListener _baseListener;
    
    public CustomWeakBaseSolution( CustomWeakBase base, double c ) {
        super( base, c );
        
        _base = base;
        _baseListener = new CustomWeakBaseListener() {
            public void strengthChanged() {
                notifyStateChanged();
            }
        };
        _base.addCustomWeakBaseListener( _baseListener );
    }
    
    public void cleanup() {
        _base.removeCustomWeakBaseListener( _baseListener );
    }
    
    public CustomWeakBase getCustomWeakBase() {
        return _base;
    }
}
