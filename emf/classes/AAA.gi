<?xml version="1.0" encoding="UTF-8"?> 
<java version="1.4.1_03" class="java.beans.XMLDecoder"> 
 <object class="edu.colorado.phet.guidedinquiry.model.GuidedInquiry"> 
  <void property="appDescriptor"> 
   <object class="edu.colorado.phet.guidedinquiry.AppDescriptor"> 
    <void property="displayName"> 
     <string>EM Field Exploration</string> 
    </void> 
    <void property="jarURLs"> 
     <array class="java.lang.String" length="1"> 
      <void index="0"> 
       <string>jar:http://Lassie:8080/phet/EMF.jar!/</string> 
      </void> 
     </array> 
    </void> 
    <void property="jnlpURL"> 
     <string>http://Lassie:8080/phet/emf.jnlp</string> 
    </void> 
   </object> 
  </void> 
  <void property="script"> 
   <void method="add"> 
    <object class="edu.colorado.phet.guidedinquiry.model.Exercise"> 
     <void property="apparatusConfig"> 
      <object class="edu.colorado.phet.guidedinquiry.model.ApparatusConfig"> 
       <void property="apparatusPanelClass"> 
        <string>edu.colorado.phet.emf.AntennaModule</string> 
       </void> 
      </object> 
     </void> 
     <void property="question"> 
      <object class="edu.colorado.phet.guidedinquiry.model.Question"> 
       <void property="answers"> 
        <void method="add"> 
         <object id="Answer0" class="edu.colorado.phet.guidedinquiry.model.Answer"> 
          <void property="text"> 
           <string>ASDF</string> 
          </void> 
         </object> 
        </void> 
        <void method="add"> 
         <object class="edu.colorado.phet.guidedinquiry.model.Answer"> 
          <void property="text"> 
           <string>QWER</string> 
          </void> 
         </object> 
        </void> 
       </void> 
       <void property="correctAnswer"> 
        <object idref="Answer0"/> 
       </void> 
       <void property="question"> 
        <string>TEETTET?</string> 
       </void> 
      </object> 
     </void> 
    </object> 
   </void> 
  </void> 
  <void property="title"> 
   <string>qwer</string> 
  </void> 
 </object> 
</java> 
