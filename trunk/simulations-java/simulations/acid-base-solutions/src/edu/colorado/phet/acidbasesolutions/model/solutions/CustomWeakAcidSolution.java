package edu.colorado.phet.acidbasesolutions.model.solutions;

import edu.colorado.phet.acidbasesolutions.model.acids.CustomWeakAcid;
import edu.colorado.phet.acidbasesolutions.model.acids.CustomWeakAcid.CustomWeakAcidListener;


public class CustomWeakAcidSolution extends WeakAcidSolution {
    
    private final CustomWeakAcid _acid;
    private final CustomWeakAcidListener _acidListener;
    
    public CustomWeakAcidSolution( CustomWeakAcid acid, double c ) {
        super( acid, c );
        
        _acid = acid;
        _acidListener = new CustomWeakAcidListener() {
            public void strengthChanged() {
                notifyStateChanged();
            }
        };
        _acid.addCustomWeakAcidListener( _acidListener );
    }
    
    public void cleanup() {
        _acid.removeCustomWeakAcidListener( _acidListener );
    }
    
    public CustomWeakAcid getCustomWeakAcid() {
        return _acid;
    }
}
