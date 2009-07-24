package edu.colorado.phet.wickettest.test;

public class BasicSimulation {
    private int id;
    private String name;
    private int type;
    private BasicProject project;

    public static final int TYPE_JAVA = 0;
    public static final int TYPE_FLASH = 1;

    public BasicSimulation() {
    }

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType( int type ) {
        this.type = type;
    }

    public BasicProject getProject() {
        return project;
    }

    public void setProject( BasicProject project ) {
        this.project = project;
    }
}
