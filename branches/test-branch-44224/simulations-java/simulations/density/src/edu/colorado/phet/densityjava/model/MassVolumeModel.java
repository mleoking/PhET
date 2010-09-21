package edu.colorado.phet.densityjava.model;

import edu.colorado.phet.densityjava.common.Unit;
import edu.colorado.phet.densityjava.common.Model;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 16, 2009
 * Time: 1:35:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class MassVolumeModel implements Model {

    private ArrayList<Unit> listeners = new ArrayList<Unit>();
    private boolean sameMass = false;

    public void setSameMass(boolean aBoolean) {
        if (aBoolean != sameMass) {
            sameMass = aBoolean;
            System.out.println("sameMass = " + sameMass);
            for (Unit listener : listeners) {
                listener.update();
            }
        }
    }

    public Boolean isSameMass() {
        return sameMass;
    }

    public boolean isSameVolume() {
        return !sameMass;
    }

    public void setSameVolume(Boolean aBoolean) {
        setSameMass(!aBoolean);
    }

    public void addListener(Unit unit) {
        listeners.add(unit);
    }
}
