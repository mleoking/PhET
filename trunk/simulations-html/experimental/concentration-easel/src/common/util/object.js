define([
    'common/ie-support'
], function(
) {
    return {
        classdef: function(className, superclass, methods) {
            var newClass = function() {
                this.initialize.apply(this, arguments);
            };

            var newProto = newClass.prototype = new superclass();
            
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
