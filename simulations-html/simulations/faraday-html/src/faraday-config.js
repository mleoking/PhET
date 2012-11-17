// Copyright 2002-2012, University of Colorado

/*
 * RequireJS configuration file for the "Faraday's Electromagnetic Lab" sim.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
require.config( {

                    deps:["faraday-main"],

                    paths:{
                        vendor:"../vendor",
                        easel:"../vendor/easeljs-0.5.0.min"
                    },

                    shim:{
                        easel:{
                            exports:"createjs"
                        }
                    }
                } );