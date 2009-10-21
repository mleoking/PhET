package edu.colorado.phet.genenetwork.model;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class CompositeModelElement implements IModelElement{
    private ArrayList<IModelElement> elements = new ArrayList<IModelElement>();
    public void add(IModelElement element){
        elements.add(element);
    }

    public Shape getShape() {
        Area area=new Area();
        for (IModelElement element : elements) {
            area.add(new Area(AffineTransform.getTranslateInstance(element.getPosition().getX(),element.getPosition().getY()).createTransformedShape(element.getShape())));
        }
        return area;
    }

    public Point2D getPosition() {
        return null;
    }

}
