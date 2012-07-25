package edu.colorado.phet.normalmodes {
public interface IView {
    function update(): void;

    function setNbrMasses(): void;

    function onModeChange(): void;
}
}
