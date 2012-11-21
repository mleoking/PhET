// Copyright 2002-2012, University of Colorado

// RequireJS configuration file for BAA-Easel.
require.config( {
                    deps:["main"],

                    paths:{
                        vendor:"../js/vendor",
                        underscore:"../js/vendor/underscore-min",
                        easel:"../js/vendor/easeljs-0.5.0.min",
                        tpl:"../js/vendor/tpl"
                    },

                    shim:{

                        underscore:{
                            exports:"_"
                        },

                        easel:{
                            exports:"createjs"
                        }
                    },

                    urlArgs: new Date().getTime()  // cache buster to make browser refresh load all included scripts

                } );