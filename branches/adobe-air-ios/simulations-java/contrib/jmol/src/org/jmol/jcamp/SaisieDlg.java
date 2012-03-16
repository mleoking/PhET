//
// Boite de dialogue elementaire
// avec etiquette, champ de texte,
// boutons Ok et Annuler
//	      (3me version)
package org.jmol.jcamp;
import java.awt.*;
public class SaisieDlg extends Dialog {
  private Button Ok;
  private Button Annuler;
  private TextField Saisie;
  public  boolean OkStatus;
  public  boolean fin;
  public SaisieDlg(Frame frame, String Titre, String nomChamp) {
    super(frame,Titre,false);
    fin=false;
    Panel text=new Panel();
    text.add(new Label(nomChamp));
    Saisie=new TextField(40);
    text.add(Saisie);
    add("North",text);
    Panel barre=new Panel();
    Ok=new Button("Ok");
    Annuler=new Button("Cancel");
    barre.add(Ok);
    barre.add(Annuler);
    add("South",barre);
    resize(500,120);
  }
 public boolean handleEvent(Event evt) {
   switch (evt.id) {
     case Event.WINDOW_DESTROY:
       fini(false);
       break;
     case Event.ACTION_EVENT:
       if (evt.target==Ok) fini(true);
       if (evt.target==Annuler) fini(false);
   }
   return super.handleEvent(evt);
 }
 public boolean keyDown(Event evt,int key) {
 
       if ((char) key=='\n') { 
              fini(true);
              return true;
                  }
       return super.keyDown(evt,key);
   }
 private void fini(boolean status) {
   OkStatus=status;
   dispose();
   fin=true;
 }
 public String lisSaisie() {
   return Saisie.getText();
 }
 public void show() {
   super.show();
   Saisie.requestFocus();
  }
}
