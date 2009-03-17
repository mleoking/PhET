package edu.colorado.phet.statesofmatter.model;

public class MoleculeType{
	private String name;
	
	public static final MoleculeType NEON = new MoleculeType("Neon");
	public static final MoleculeType ARGON = new MoleculeType("Argon");
	public static final MoleculeType OXYGEN = new MoleculeType("Oxygen");
	public static final MoleculeType ADJUSTABLE = new MoleculeType("Adjustable");

	public MoleculeType(String name) {
		this.name = name;
	}
	
	public String toString(){
		return name;
	}
}

