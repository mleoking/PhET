import org.aswing.util.ObjectUtils;
/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
/**
 * Reflection Utils.
 * @author iiley
 */
class org.aswing.util.Reflection {
    
    /**
     * Return the class object by the specified class name.
     * <p>Then you can use this way to create a new instance of this class:
     * <pre>
     *     var classConstructor:Function = Reflection.getClass("your_class_name");
     *     var instance:YourClass = YourClass(new classObj());
     * </pre>
     * Or call it's static method from this way:
     * <pre>
     *     var classConstructor:Function = Reflection.getClass("your_class_name");
     *     classConstructor.itsStaticMethod(args);
     * </pre>
     * 
     * @param fullname the class's full name include package. For example "org.aswing.Component"
     * @return the class object of the name
     */
    public static function getClass(fullname:String):Function{
        var parts:Array = fullname.split(".");
        var classObj:Object = _global;
        for(var i:Number=0; i<parts.length; i++){
            classObj = classObj[parts[i]];
        }
        return Function(classObj);
    }
    
    /**
     * Returns is <code>subClass</code> is a sub class of <code>superClass</code>.
     */
    public static function isSubClass(superClass:Function, subClass:Function):Boolean{
        var proto:Object = subClass.prototype;
        while(proto.__proto__ != undefined){
            if(proto.__proto__ === superClass.prototype){
                return true;
            }
            proto = proto.__proto__;
        }
        return false;
    }
    
    /**
     * Sets passed-in <code>value</code> to the specified property of the <code>instance</code>.
     * Tries to recognize setter method by adding "set" prefix to the property name.
     * If specified property not found throws an exception. 
     * 
     * @param instance the object's instance
     * @param propertyName the instance's property name to set new value
     * @param value the value to set
     */
    public static function setProperty(instance:Object, propertyName:String, value):Void {
        
        // get capitalized property name
        var capitalizedPropertyName:String = propertyName.charAt(0).toUpperCase() + propertyName.substring(1);
        
        // checks for getter method
        var setterName:String = "set" + capitalizedPropertyName;
        
        if (instance[setterName] != null && ObjectUtils.isFunction(instance[setterName])) {
            instance[setterName](value);
        } else {
        	throw new Error("Property \""+propertyName+"\" not found");	
        }
    }
    
    /**
     * Returns the property value of the instance. Tries to recognize the appropriate
     * getter method by adding "get" prefix to the property name (also tries "is" and "has"
     * prefixes for properties had <code>Boolean). 
     * 
     * @param instance the instance name contained property
     * @param propertyName the name of the property to get
     * @return the value of the property or <code>undefined</code> if specified
     * property doesn't exist. 
     */
    public static function getProperty(instance:Object, propertyName:String) {
        
        // get capitalized property name
        var capitalizedPropertyName:String = propertyName.charAt(0).toUpperCase() + propertyName.substring(1);
        
        // checks for getter method
        var getterName:String = "get" + capitalizedPropertyName;
        
        // try specific headers for Boolean
        if (instance[getterName] == null) {
            getterName = "is" + capitalizedPropertyName;
        }
        if (instance[getterName] == null) {
            getterName = "has" + capitalizedPropertyName;
        }
        
        return (instance[getterName] != null && ObjectUtils.isFunction(instance[getterName])) ? instance[getterName]() : undefined;
    }
     
    /**
     * Creates a new instance of the passed-in constructor function and applying the
     * passed-in arguments to the constructor.
     * 
     * If <code>constructor</code> is <code>undefined</code> throws an exception. 
     * 
     * Note, this method works only with <b>MTASC</b> and doesn't allow to instanciate
     * build-in types (like <code>String</code>, <code>Array</code>, <code>Date</code> and etc.)
     *
     * @param constructor the reference to Class constructor to be instanciated
     * @param args the arguments to be applied to the constructor
     * @return new instance of the class
     */
    public static function createInstance(constructor:Function, args:Array) {
        if (constructor == undefined) {
        	throw new Error("Constructor is undefined");	
        }
        
        var instance:Object = new Object();
        instance.__proto__ = constructor.prototype;
        instance.__constructor__ = constructor;
        
        constructor.apply(instance, args);
        return instance;
    }
     
}
