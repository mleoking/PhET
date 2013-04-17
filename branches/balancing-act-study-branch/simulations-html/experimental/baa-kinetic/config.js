require.config({
  deps: ["main"],

  paths: {
    vendor: "../js/vendor",
    plugins: "../js/plugins",

    underscore: "../js/vendor/underscore-min",
    kinetic: "../js/vendor/kinetic-v4.0.4"

  },

  shim: {

    underscore: {
      exports: "_"
    },

    kinetic: {
      exports: "Kinetic"
    }

  }
});

