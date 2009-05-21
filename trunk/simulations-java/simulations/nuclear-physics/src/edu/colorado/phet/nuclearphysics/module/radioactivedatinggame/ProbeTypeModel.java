package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

public class ProbeTypeModel extends SimpleObservable{
	
	public static final ProbeType [] POSSIBLE_PROBE_TYPES = new ProbeType[] {
		ProbeType.CARBON_14,
		ProbeType.URANIUM_238,
		ProbeType.CUSTOM 
	};

	private ProbeType _probeType;
	
	public ProbeTypeModel(){
		// Set the default.
		this._probeType = ProbeType.CARBON_14;
	}
	
	public ProbeType getProbeType() {
		return _probeType;
	}

	public void setProbeType(ProbeType type) {
		if (!_probeType.equals(type)){
			_probeType = type;
			notifyObservers();
		}
	}

	public abstract static class ProbeType{
		
		public static final ProbeType CARBON_14 = new ProbeType("Carbon 14", Carbon14Nucleus.HALF_LIFE){
			public double getPercentage(DatableObject item) {
				return item.getPercentageCarbon14Remaining(item);
			}	
		};
		public static final ProbeType URANIUM_238 = new ProbeType("Uranium 238", Carbon14Nucleus.HALF_LIFE){
			public double getPercentage(DatableObject item) {
				return item.getPercentageUranium238Remaining(item);
			}
		};
		public static final ProbeType CUSTOM = new ProbeType("Custom", MultiNucleusDecayModel.convertYearsToMs(100000)){
			public double getPercentage(DatableObject item) {
				return item.getPercentageCustomNucleusRemaining(item, _halfLife);
			}	
		};
		
		String _name;
		double _halfLife;
		
		public ProbeType(String name, double halfLife){
			_name = name;
			_halfLife = halfLife;
		}
		
		public String toString(){
			return _name;
		}
		
		public String getName(){
			return _name;
		}
		
		public double getHalfLife(){
			return _halfLife;
		}
		
		public boolean equals(Object object){
			boolean result = false;
			if (object instanceof ProbeType){
				ProbeType probeType = (ProbeType)object;
				if ((probeType._halfLife == _halfLife) && (probeType._name.equals(_name))){
					result = true;
				}
			}
			return result;
		}

		public abstract double getPercentage(DatableObject item);
	}

	public double getPercentage(DatableObject item) {
		return _probeType.getPercentage(item);
	}
}
