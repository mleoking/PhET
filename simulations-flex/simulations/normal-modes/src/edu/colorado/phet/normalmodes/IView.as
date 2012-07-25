/*
 * Copyright 2002-2012, University of Colorado
 */

package edu.colorado.phet.normalmodes {
public interface IView {
    function update(): void;

    function setNbrMasses(): void;

    function onModeChange(): void;
}
}
