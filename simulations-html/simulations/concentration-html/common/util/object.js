define([
    'common/polyfills'
], function(
) {
    return {
        classdef: function(className, superclass, methods) {
            var newClass = function() {
                this.initialize.apply(this, arguments);
            };
            
            // Create a copy of superclass without calling its constructor
            var bareSuper = function() {};
            bareSuper.prototype = superclass.prototype;
            var newProto = newClass.prototype = new bareSuper();
            
            // experimental debugging aid; may not actually be useful -PPC
            newProto._className = className;
            
            methods.initialize = methods.initialize || function () {};

            // Add subclass methods
            for(var m in methods) {
                if(newProto[m])
                    newProto[className + '_super_' + m] = newProto[m];
                newProto[m] = methods[m];
            }

            return newClass;
        }
    }
});
