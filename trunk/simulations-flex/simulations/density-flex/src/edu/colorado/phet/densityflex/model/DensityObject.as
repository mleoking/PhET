package edu.colorado.phet.densityflex.model {
import Box2D.Common.Math.b2Vec2;
import Box2D.Dynamics.Contacts.b2ContactResult;
import Box2D.Dynamics.b2Body;

import away3d.core.base.Object3D;

import edu.colorado.phet.densityflex.view.DensityView3D;

public class DensityObject {

    private var x:Number;
    private var y:Number;
    private var z:Number;
    private var listeners:Array;
    private var model:DensityModel;

    private var body:b2Body;

    public function DensityObject(x:Number, y:Number, z:Number, model:DensityModel) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.model = model;
        this.listeners = new Array();
    }

    public function addListener(listener:Listener):void {
        listeners.push(listener);
    }

    public function getX():Number {
        return x;
    }

    public function getY():Number {
        return y;
    }

    public function getZ():Number {
        return z;
    }

    public function update():void {
        setPosition(body.GetPosition().x, body.GetPosition().y);
    }

    public function remove():void {
        for each(var listener:Listener in listeners) {
            listener.remove();
        }
    }

    public function setPosition(x:Number, y:Number):void {
        this.x = x;
        this.y = y;

        if (body.GetPosition().x != x || body.GetPosition().y != y) {
            body.SetXForm(new b2Vec2(x, y), 0);
        }

        notifyListeners();
    }

    protected function notifyListeners():void {
        //todo: notify listeners
        // TODO: looks like major potential for infinite loops here, since update => setPosition => Update is possible
        for each (var listener:Listener in listeners) {
            listener.update();
        }
    }

    public function getBody():b2Body {
        return body;
    }

    public function setBody(body:b2Body):void {
        if (this.body != null) {
            //delete from world
            getModel().getWorld().DestroyBody(this.body);
        }
        this.body = body;
    }

    public function getModel():DensityModel {
        return model;
    }

    public function registerContact(contact:b2ContactResult):void {

    }

    public function resetContacts():void {

    }

    public function createNode(view:DensityView3D):Object3D {
        throw new Error();
    }
}
}