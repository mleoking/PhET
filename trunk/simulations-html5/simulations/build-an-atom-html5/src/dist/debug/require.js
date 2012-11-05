/**
 * almond 0.2.0 Copyright (c) 2011, The Dojo Foundation All Rights Reserved.
 * Available via the MIT or new BSD license.
 * see: http://github.com/jrburke/almond for details
 */
//Going sloppy to avoid 'use strict' string cost, but strict practices should
//be followed.
/*jslint sloppy: true */
/*global setTimeout: false */

var requirejs, require, define;
(function (undef) {
    var main, req, makeMap, handlers,
        defined = {},
        waiting = {},
        config = {},
        defining = {},
        aps = [].slice;

    /**
     * Given a relative module name, like ./something, normalize it to
     * a real name that can be mapped to a path.
     * @param {String} name the relative name
     * @param {String} baseName a real name that the name arg is relative
     * to.
     * @returns {String} normalized name
     */
    function normalize(name, baseName) {
        var nameParts, nameSegment, mapValue, foundMap,
            foundI, foundStarMap, starI, i, j, part,
            baseParts = baseName && baseName.split("/"),
            map = config.map,
            starMap = (map && map['*']) || {};

        //Adjust any relative paths.
        if (name && name.charAt(0) === ".") {
            //If have a base name, try to normalize against it,
            //otherwise, assume it is a top-level require that will
            //be relative to baseUrl in the end.
            if (baseName) {
                //Convert baseName to array, and lop off the last part,
                //so that . matches that "directory" and not name of the baseName's
                //module. For instance, baseName of "one/two/three", maps to
                //"one/two/three.js", but we want the directory, "one/two" for
                //this normalization.
                baseParts = baseParts.slice(0, baseParts.length - 1);

                name = baseParts.concat(name.split("/"));

                //start trimDots
                for (i = 0; i < name.length; i += 1) {
                    part = name[i];
                    if (part === ".") {
                        name.splice(i, 1);
                        i -= 1;
                    } else if (part === "..") {
                        if (i === 1 && (name[2] === '..' || name[0] === '..')) {
                            //End of the line. Keep at least one non-dot
                            //path segment at the front so it can be mapped
                            //correctly to disk. Otherwise, there is likely
                            //no path mapping for a path starting with '..'.
                            //This can still fail, but catches the most reasonable
                            //uses of ..
                            break;
                        } else if (i > 0) {
                            name.splice(i - 1, 2);
                            i -= 2;
                        }
                    }
                }
                //end trimDots

                name = name.join("/");
            }
        }

        //Apply map config if available.
        if ((baseParts || starMap) && map) {
            nameParts = name.split('/');

            for (i = nameParts.length; i > 0; i -= 1) {
                nameSegment = nameParts.slice(0, i).join("/");

                if (baseParts) {
                    //Find the longest baseName segment match in the config.
                    //So, do joins on the biggest to smallest lengths of baseParts.
                    for (j = baseParts.length; j > 0; j -= 1) {
                        mapValue = map[baseParts.slice(0, j).join('/')];

                        //baseName segment has  config, find if it has one for
                        //this name.
                        if (mapValue) {
                            mapValue = mapValue[nameSegment];
                            if (mapValue) {
                                //Match, update name to the new value.
                                foundMap = mapValue;
                                foundI = i;
                                break;
                            }
                        }
                    }
                }

                if (foundMap) {
                    break;
                }

                //Check for a star map match, but just hold on to it,
                //if there is a shorter segment match later in a matching
                //config, then favor over this star map.
                if (!foundStarMap && starMap && starMap[nameSegment]) {
                    foundStarMap = starMap[nameSegment];
                    starI = i;
                }
            }

            if (!foundMap && foundStarMap) {
                foundMap = foundStarMap;
                foundI = starI;
            }

            if (foundMap) {
                nameParts.splice(0, foundI, foundMap);
                name = nameParts.join('/');
            }
        }

        return name;
    }

    function makeRequire(relName, forceSync) {
        return function () {
            //A version of a require function that passes a moduleName
            //value for items that may need to
            //look up paths relative to the moduleName
            return req.apply(undef, aps.call(arguments, 0).concat([relName, forceSync]));
        };
    }

    function makeNormalize(relName) {
        return function (name) {
            return normalize(name, relName);
        };
    }

    function makeLoad(depName) {
        return function (value) {
            defined[depName] = value;
        };
    }

    function callDep(name) {
        if (waiting.hasOwnProperty(name)) {
            var args = waiting[name];
            delete waiting[name];
            defining[name] = true;
            main.apply(undef, args);
        }

        if (!defined.hasOwnProperty(name) && !defining.hasOwnProperty(name)) {
            throw new Error('No ' + name);
        }
        return defined[name];
    }

    //Turns a plugin!resource to [plugin, resource]
    //with the plugin being undefined if the name
    //did not have a plugin prefix.
    function splitPrefix(name) {
        var prefix,
            index = name ? name.indexOf('!') : -1;
        if (index > -1) {
            prefix = name.substring(0, index);
            name = name.substring(index + 1, name.length);
        }
        return [prefix, name];
    }

    /**
     * Makes a name map, normalizing the name, and using a plugin
     * for normalization if necessary. Grabs a ref to plugin
     * too, as an optimization.
     */
    makeMap = function (name, relName) {
        var plugin,
            parts = splitPrefix(name),
            prefix = parts[0];

        name = parts[1];

        if (prefix) {
            prefix = normalize(prefix, relName);
            plugin = callDep(prefix);
        }

        //Normalize according
        if (prefix) {
            if (plugin && plugin.normalize) {
                name = plugin.normalize(name, makeNormalize(relName));
            } else {
                name = normalize(name, relName);
            }
        } else {
            name = normalize(name, relName);
            parts = splitPrefix(name);
            prefix = parts[0];
            name = parts[1];
            if (prefix) {
                plugin = callDep(prefix);
            }
        }

        //Using ridiculous property names for space reasons
        return {
            f: prefix ? prefix + '!' + name : name, //fullName
            n: name,
            pr: prefix,
            p: plugin
        };
    };

    function makeConfig(name) {
        return function () {
            return (config && config.config && config.config[name]) || {};
        };
    }

    handlers = {
        require: function (name) {
            return makeRequire(name);
        },
        exports: function (name) {
            var e = defined[name];
            if (typeof e !== 'undefined') {
                return e;
            } else {
                return (defined[name] = {});
            }
        },
        module: function (name) {
            return {
                id: name,
                uri: '',
                exports: defined[name],
                config: makeConfig(name)
            };
        }
    };

    main = function (name, deps, callback, relName) {
        var cjsModule, depName, ret, map, i,
            args = [],
            usingExports;

        //Use name if no relName
        relName = relName || name;

        //Call the callback to define the module, if necessary.
        if (typeof callback === 'function') {

            //Pull out the defined dependencies and pass the ordered
            //values to the callback.
            //Default to [require, exports, module] if no deps
            deps = !deps.length && callback.length ? ['require', 'exports', 'module'] : deps;
            for (i = 0; i < deps.length; i += 1) {
                map = makeMap(deps[i], relName);
                depName = map.f;

                //Fast path CommonJS standard dependencies.
                if (depName === "require") {
                    args[i] = handlers.require(name);
                } else if (depName === "exports") {
                    //CommonJS module spec 1.1
                    args[i] = handlers.exports(name);
                    usingExports = true;
                } else if (depName === "module") {
                    //CommonJS module spec 1.1
                    cjsModule = args[i] = handlers.module(name);
                } else if (defined.hasOwnProperty(depName) ||
                           waiting.hasOwnProperty(depName) ||
                           defining.hasOwnProperty(depName)) {
                    args[i] = callDep(depName);
                } else if (map.p) {
                    map.p.load(map.n, makeRequire(relName, true), makeLoad(depName), {});
                    args[i] = defined[depName];
                } else {
                    throw new Error(name + ' missing ' + depName);
                }
            }

            ret = callback.apply(defined[name], args);

            if (name) {
                //If setting exports via "module" is in play,
                //favor that over return value and exports. After that,
                //favor a non-undefined return value over exports use.
                if (cjsModule && cjsModule.exports !== undef &&
                        cjsModule.exports !== defined[name]) {
                    defined[name] = cjsModule.exports;
                } else if (ret !== undef || !usingExports) {
                    //Use the return value from the function.
                    defined[name] = ret;
                }
            }
        } else if (name) {
            //May just be an object definition for the module. Only
            //worry about defining if have a module name.
            defined[name] = callback;
        }
    };

    requirejs = require = req = function (deps, callback, relName, forceSync, alt) {
        if (typeof deps === "string") {
            if (handlers[deps]) {
                //callback in this case is really relName
                return handlers[deps](callback);
            }
            //Just return the module wanted. In this scenario, the
            //deps arg is the module name, and second arg (if passed)
            //is just the relName.
            //Normalize module name, if it contains . or ..
            return callDep(makeMap(deps, callback).f);
        } else if (!deps.splice) {
            //deps is a config object, not an array.
            config = deps;
            if (callback.splice) {
                //callback is an array, which means it is a dependency list.
                //Adjust args if there are dependencies
                deps = callback;
                callback = relName;
                relName = null;
            } else {
                deps = undef;
            }
        }

        //Support require(['a'])
        callback = callback || function () {};

        //If relName is a function, it is an errback handler,
        //so remove it.
        if (typeof relName === 'function') {
            relName = forceSync;
            forceSync = alt;
        }

        //Simulate async callback;
        if (forceSync) {
            main(undef, deps, callback, relName);
        } else {
            setTimeout(function () {
                main(undef, deps, callback, relName);
            }, 15);
        }

        return req;
    };

    /**
     * Just drops the config on the floor, but returns req in case
     * the config return value is used.
     */
    req.config = function (cfg) {
        config = cfg;
        return req;
    };

    define = function (name, deps, callback) {

        //This module may not have dependencies
        if (!deps.splice) {
            //deps is not an array, so probably means
            //an object literal or factory function for
            //the value. Adjust args.
            callback = deps;
            deps = [];
        }

        waiting[name] = [name, deps, callback];
    };

    define.amd = {
        jQuery: true
    };
}());

define("electron_shell",[],function(){function e(e){this.location=e,this.radius=120}return e.prototype.draw=function(e){var t=this.location.x,n=this.location.y,r=e.createRadialGradient(t,n,0,t,n,this.radius);r.addColorStop(0,"rgba( 0, 0, 200, 0.2)"),r.addColorStop(1,"rgba( 0, 0, 200, 0.05)"),e.fillStyle=r,e.beginPath(),e.arc(t,n,this.radius,0,Math.PI*2,!0),e.closePath(),e.fill()},e.prototype.setLocationComponents=function(e,t){this.location.x=e,this.location.y=t},e.prototype.setLocation=function(e){this.setLocationComponents(e.x,e.y)},e.prototype.containsPoint=function(e){return Math.sqrt(Math.pow(e.x-this.location.x,2)+Math.pow(e.y-this.location.y,2))<this.radius},e}),define("point2d",[],function(){function e(e,t){this.x=e,this.y=t}return e.prototype.toString=function(){return this.x+", "+this.y},e.prototype.setComponents=function(e,t){this.x=e,this.y=t},e.prototype.set=function(e){this.setComponents(e.x,e.y)},e.prototype.equals=function(e){return e.x==this.x&&e.y==this.y},e}),define("nucleon",["point2d"],function(e){function n(n){this.location=new e(0,0),this.radius=t,this.color=n}var t=20;return n.prototype.draw=function(e){var t=this.location.x,n=this.location.y,r=e.createRadialGradient(t-this.radius/3,n-this.radius/3,0,t,n,this.radius);r.addColorStop(0,"white"),r.addColorStop(1,this.color),e.fillStyle=r,e.beginPath(),e.arc(t,n,this.radius,0,Math.PI*2,!0),e.closePath(),e.fill()},n.prototype.setLocation=function(e){this.setLocationComponents(e.x,e.y)},n.prototype.setLocationComponents=function(e,t){this.location.x=e,this.location.y=t},n.prototype.containsPoint=function(e){return Math.sqrt(Math.pow(e.x-this.location.x,2)+Math.pow(e.y-this.location.y,2))<this.radius},n}),define("bucket",["point2d","nucleon"],function(e,t){function i(e,t,r){this.location=e,this.color=t,this.labelText=r,this.width=n*9,this.height=this.width*.35,this.nucleonsInBucket=[]}var n=20,r=10;return i.prototype.drawFront=function(e){var t=this.location.x,n=this.location.y,r=e.createLinearGradient(t,n,t+this.width,n);r.addColorStop(0,"white"),r.addColorStop(1,this.color),e.beginPath(),e.moveTo(t,n),e.lineTo(t+this.width*.15,n+this.height),e.bezierCurveTo(t+this.width*.4,n+this.height*1.1,t+this.width*.6,n+this.height*1.1,t+this.width*.85,n+this.height),e.lineTo(t+this.width,n),e.bezierCurveTo(t+this.width*.9,n+this.height*.2,t+this.width*.1,n+this.height*.2,t,n),e.closePath(),e.fillStyle=r,e.fill(),e.fillStyle="#000",e.font="22px sans-serif",e.textAlign="center",e.textBaseline="middle",e.fillText(this.labelText,this.location.x+this.width/2,this.location.y+this.height/2)},i.prototype.drawInterior=function(e){var t=this.location.x,n=this.location.y,r=e.createLinearGradient(t,n,t+this.width,n);r.addColorStop(0,this.color),r.addColorStop(1,"gray"),e.beginPath(),e.moveTo(t,n),e.bezierCurveTo(t+this.width*.1,n-this.height*.2,t+this.width*.9,n-this.height*.2,t+this.width,n),e.bezierCurveTo(t+this.width*.9,n+this.height*.2,t+this.width*.1,n+this.height*.2,t,n),e.fillStyle=r,e.fill()},i.prototype.addNucleonToBucket=function(e){e.setLocation(this.getNextOpenNucleonLocation()),this.nucleonsInBucket.push(e)},i.prototype.removeNucleonFromBucket=function(e){var t=this.nucleonsInBucket.indexOf(e);t!=-1&&this.nucleonsInBucket.splice(t,1)},i.prototype.removeAllParticles=function(){this.nucleonsInBucket.length=0},i.prototype.getNucleonLocationByIndex=function(n){var r,i=(new t("black")).radius,s=Math.round((this.width-2*i)/(i*2));return n<s-1?r=new e(this.location.x+i*2.5+n*i*2,this.location.y+i*.5):n<2*s-1?r=new e(this.location.x+i*1.5+(n-s+1)*i*2,this.location.y):n<3*s-2?r=new e(this.location.x+i*2.5+(n-s*2+1)*i*2,this.location.y-i*.5):(console.log("bucket capacity exceeded, using center"),r=new e(this.location.x+this.width/2,this.location.y)),r},i.prototype.getNextOpenNucleonLocation=function(){var e;for(var t=0;t<r;t++){e=this.getNucleonLocationByIndex(t);var n=!1;for(var i=0;i<this.nucleonsInBucket.length;i++)if(this.nucleonsInBucket[i].location.equals(e)){n=!0;break}if(!n)break}return e},i.prototype.setLocationComponents=function(e,t){this.location.x=e,this.location.y=t},i.prototype.setLocation=function(e){this.setLocationComponents(e.x,e.y)},i.prototype.containsPoint=function(e){return e.x>this.location.x&&e.x<this.location.x+this.width&&e.y>this.location.y&&e.y<this.location.y+this.height},i}),define("reset_button",[],function(){function e(e,t,n){this.location=e,this.width=90,this.height=40,this.color=t,this.pressed=!1,this.reset=n}return e.prototype.draw=function(e){var t=this.location.x,n=this.location.y,r=e.createLinearGradient(t,n,t,n+this.height);this.pressed?r.addColorStop(0,this.color):(r.addColorStop(0,"white"),r.addColorStop(1,this.color)),e.strokeStyle="#222",e.lineWidth=1,e.strokeRect(t,n,this.width,this.height),e.fillStyle=r,e.fillRect(t,n,this.width,this.height),e.fillStyle="#000",e.font="28px sans-serif",e.textBaseline="top",e.textAlign="left",e.fillText("Reset",t+5,n+5)},e.prototype.press=function(){this.pressed=!0,this.reset()},e.prototype.unPress=function(e){this.pressed=!1},e.prototype.setLocationComponents=function(e,t){this.location.x=e,this.location.y=t},e.prototype.setLocation=function(e){this.setLocationComponents(e.x,e.y)},e.prototype.containsPoint=function(e){return e.x>this.location.x&&e.x<this.location.x+this.width&&e.y>this.location.y&&e.y<this.location.y+this.height},e}),define("nucleus_label",[],function(){function e(e,t){this.location=e,this.text="",this.nucleonsInNucleus=t}return e.prototype.draw=function(e){var t=0,n=0;for(var r=0;r<this.nucleonsInNucleus.length;r++)this.nucleonsInNucleus[r].color=="red"?t++:this.nucleonsInNucleus[r].color=="gray"&&n++;this.updateText(t,n),e.fillStyle="#000",e.font="28px sans-serif",e.textBaseline="top",e.textAlign="left",e.fillText(this.text,this.location.x,this.location.y)},e.prototype.updateText=function(e,t){switch(e){case 0:this.text="";break;case 1:this.text="Hydrogen",t==0||t==1?this.text+=" (Stable)":this.text+=" (Unstable)";break;case 2:this.text="Helium",t==1||t==2?this.text+=" (Stable)":this.text+=" (Unstable)";break;case 3:this.text="Lithium",t==3||t==4?this.text+=" (Stable)":this.text+=" (Unstable)";break;case 4:this.text="Beryllium",t==5?this.text+=" (Stable)":this.text+=" (Unstable)";break;case 5:this.text="Boron",t==5||t==6?this.text+=" (Stable)":this.text+=" (Unstable)";break;case 6:this.text="Carbon",t==6||t==7?this.text+=" (Stable)":this.text+=" (Unstable)";break;case 7:this.text="Nitrogen",t==7||t==8?this.text+=" (Stable)":this.text+=" (Unstable)";break;case 8:this.text="Oxygen",t==8||t==9||t==10?this.text+=" (Stable)":this.text+=" (Unstable)";break;case 9:this.text="Fluorine",t==10?this.text+=" (Stable)":this.text+=" (Unstable)";break;case 10:this.text="Neon",t==10||t==11||t==12?this.text+=" (Stable)":this.text+=" (Unstable)";break;default:this.text="Phetium - "+(e+t)}},e.prototype.setLocationComponents=function(e,t){this.location.x=e,this.location.y=t},e.prototype.setLocation=function(e){this.setLocationComponents(e.x,e.y)},e}),define("proton",["nucleon"],function(e){function t(){e.call(this,"red")}return t.prototype=e.prototype,t}),define("neutron",["nucleon"],function(e){function t(){e.call(this,"gray")}return t.prototype=e.prototype,t}),define("caat-init",[],function(){function e(){console.log("Hello from CAAT woman."),CAAT.modules.initialization=CAAT.modules.initialization||{},CAAT.modules.initialization.init=function(e,t,n,r,i){var s=document.getElementById(n),o;if(CAAT.__CSS__)s&&0==s instanceof HTMLDivElement&&(s=null),s===null&&(s=document.createElement("div"),document.body.appendChild(s)),o=(new CAAT.Director).initialize(e||800,t||600,s);else{if(s){if(s instanceof HTMLDivElement){var u=document.createElement("canvas");s.appendChild(u),s=u}else if(0==s instanceof HTMLCanvasElement){var u=document.createElement("canvas");document.body.appendChild(u),s=u}}else s=document.createElement("canvas"),document.body.appendChild(s);CAAT.DEBUG=1,o=(new CAAT.Director).initialize(e||800,t||600,s)}(new CAAT.ImagePreloader).loadImages(r,function(t,n){t==n.length&&(o.emptyScenes(),o.setImagesCache(n),i(o),o.setScene(0),CAAT.loop(60))})}}return e}),require(["electron_shell","bucket","point2d","reset_button","nucleus_label","nucleon","proton","neutron","caat-init"],function(e,t,n,r,s,o,u,a,f){function L(){if("WebSocket"in window){var e=new WebSocket("ws://localhost:8887/echo");e.onmessage=function(e){document.location.reload(!0)},e.onclose=function(){},console.log("opened websocket")}else alert("WebSocket NOT supported by your Browser!")}function O(){A()}function M(){L(),d=$("#canvas")[0],d.getContext&&(m=d.getContext("2d")),console.log("About to init CAAT"),f(),document.onmousedown=q,document.onmouseup=R,document.onmousemove=U,document.addEventListener("touchstart",z,!1),document.addEventListener("touchmove",W,!1),document.addEventListener("touchend",X,!1),S=new e(new n(325,150)),y=new t(new n(100,300),"gray","Neutrons"),b=new t(new n(400,300),"red","Protons"),E=new r(new n(600,325),"orange",H),x=new s(new n(450,80),g),H(),window.addEventListener("deviceorientation",V,!1),document.addEventListener("touchmove",function(e){e.preventDefault()},!1),O()}function _(){m.save(),m.globalCompositeOperation="source-over",m.fillStyle="rgb(255, 255, 153)",m.fillRect(0,0,Math.max(2e3,C),Math.max(2e3,k)),m.restore()}function D(){m.fillStyle="#00f",m.font="30px sans-serif",m.textBaseline="top",m.textAlign="left",m.fillText("Build an Atom",10,10)}function P(){m.fillStyle="#f80",m.font="italic 20px sans-serif",m.textBaseline="top",m.fillText("PhET",T-70,N-30)}function H(){F(),b.removeAllParticles(),y.removeAllParticles();for(var e=0;e<l;e++)b.addNucleonToBucket(new u);for(var e=0;e<c;e++)y.addNucleonToBucket(new a)}function B(){_(),D(),P(),S.draw(m),E.draw(m),y.drawInterior(m),b.drawInterior(m);for(var e=0;e<g.length;e++)g[e].draw(m);var t=b.nucleonsInBucket.slice();t.reverse();for(e=0;e<t.length;e++)t[e].draw(m);t=y.nucleonsInBucket.slice(),t.reverse();for(e=0;e<t.length;e++)t[e].draw(m);w!==null&&w.draw(m),y.drawFront(m),b.drawFront(m),x.draw(m)}function j(e){for(i=0;i<g.length;i++)if(g[i]==e){g.splice(i,1);break}I()}function F(){g.length=0}function I(){var e=(new o("black")).radius;if(g.length==0)return;if(g.length==1)g[0].setLocation(S.location);else if(g.length==2)g[0].setLocationComponents(S.location.x-e,S.location.y),g[1].setLocationComponents(S.location.x+e,S.location.y);else if(g.length==3)g[0].setLocationComponents(S.location.x,S.location.y-e*1.1),g[1].setLocationComponents(S.location.x+e*.77,S.location.y+e*.77),g[2].setLocationComponents(S.location.x-e*.77,S.location.y+e*.77);else if(g.length==4)g[0].setLocationComponents(S.location.x,S.location.y-e*1.5),g[1].setLocationComponents(S.location.x+e,S.location.y),g[2].setLocationComponents(S.location.x-e,S.location.y),g[3].setLocationComponents(S.location.x,S.location.y+e*1.5);else if(g.length>=5){g[g.length-1].setLocationComponents(S.location.x,S.location.y),g[g.length-2].setLocationComponents(S.location.x,S.location.y-e*1.5),g[g.length-3].setLocationComponents(S.location.x+e,S.location.y),g[g.length-4].setLocationComponents(S.location.x-e,S.location.y),g[g.length-5].setLocationComponents(S.location.x,S.location.y+e*1.5);var t=e*2;for(i=g.length-6;i>=0;i--){var n=Math.random()*Math.PI*2;g[i].setLocationComponents(S.location.x+t*Math.cos(n),S.location.y+t*Math.sin(n))}}}function q(e){J(new n(e.clientX,e.clientY))}function R(e){Q()}function U(e){K(new n(e.clientX,e.clientY))}function z(e){e.touches.length==1&&(e.preventDefault(),J(new n(e.touches[0].pageX,e.touches[0].pageY)))}function W(e){e.touches.length==1&&(e.preventDefault(),K(new n(e.touches[0].pageX,e.touches[0].pageY)))}function X(e){Q()}function V(e){}function J(e){v=!0,w=null;for(var t=0;t<g.length;t++)if(g[t].containsPoint(e)){w=g[t],j(w);break}if(w==null)for(var t=0;t<b.nucleonsInBucket.length;t++)if(b.nucleonsInBucket[t].containsPoint(e)){w=b.nucleonsInBucket[t],b.removeNucleonFromBucket(w);break}if(w==null)for(var t=0;t<y.nucleonsInBucket.length;t++)if(y.nucleonsInBucket[t].containsPoint(e)){w=y.nucleonsInBucket[t],y.removeNucleonFromBucket(w);break}w!=null?w.setLocation(e):E.containsPoint(e)&&E.press(),B()}function K(e){v&&w!=null&&(w.setLocation(e),B())}function Q(){v=!1,w!=null&&(S.containsPoint(w.location)?(g.push(w),I()):w instanceof u?b.addNucleonToBucket(w):y.addNucleonToBucket(w),w=null),E.pressed&&E.unPress(),B()}var l=10,c=10,h=20,p=10,d,v=!1,m,g=[],y,b,w=null,E,S,x,T=0,N=0,C=0,k=0,A=function(){if(window.innerWidth!==T||window.innerHeight!==N)T=window.innerWidth,d.width=T,N=window.innerHeight?window.innerHeight:$(window).height(),d.height=N,window.scrollTo(0,0),T>C&&(C=T),N>k&&(k=N),B()};$(document).ready(function(){M()}),$(window).resize(O)}),define("main",function(){}),require.config({deps:["main"],paths:{vendor:"../js/vendor",plugins:"../js/plugins",underscore:"../js/vendor/underscore-min",tpl:"../js/plugins/tpl",text:"../js/plugins/text",json:"../js/plugins/json"},shim:{underscore:{exports:"_"},"main-deprecated":[]}}),define("config",function(){})