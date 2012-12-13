# Build an Atom Simulator

v0.0.0

## Installation

* [Node.js v0.8.x](http://nodejs.org/download/) and `npm`

To install all dependencies, run

    npm install

To run the production build process, make sure `grunt` is installed
globally.

    npm install --global grunt

To build for production, run

    grunt

Expected output:

    Running "requirejs:compile" (requirejs) task
    >> Tracing dependencies for: config
    [...]

    >> /app/config.js

    Running "concat:dist/debug/require.js" (concat) task
    File "dist/debug/require.js" created.

    Running "min:dist/release/require.js" (min) task
    File "dist/release/require.js" created.
    Uncompressed size: 13505 bytes.
    Compressed size: 1326 bytes gzipped (2844 bytes minified).

    Done, without errors.
