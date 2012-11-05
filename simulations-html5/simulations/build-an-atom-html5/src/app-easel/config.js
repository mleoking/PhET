// Copyright 2002-2012, University of Colorado
require.config({
  deps: ["main"],

  paths: {
    vendor: "../js/vendor",
    plugins: "../js/plugins",

    underscore: "../js/vendor/underscore-min",
    easel: "../js/vendor/easeljs-0.5.0"
  },

  shim: {

    underscore: {
      exports: "_"
    },

    easel: {
      exports: "createjs"
    }

  }
});

