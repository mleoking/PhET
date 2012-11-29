// Copyright 2002-2012, University of Colorado

// RequireJS configuration file for BAA-Easel.
require.config( {
                    deps:["build-an-atom-main"],

                    paths:{
                        underscore:"../../../../contrib/underscore-1.4.2",
                        easel: "../../../../contrib/easel-0.5.0",
                        tpl:"../../../../contrib/tpl-0.2"
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
