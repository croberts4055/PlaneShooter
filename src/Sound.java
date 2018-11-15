// Courtesy of Java-Gaming.org user Phased. http://www.java-gaming.org/index.php?topic=27473.0

import java.applet.Applet;
import java.applet.AudioClip;

public class Sound {
   private AudioClip clip;
   public Sound(String name){
      try
      {
         clip = Applet.newAudioClip(Game.class.getResource(name));
      }catch (Throwable e){
         e.printStackTrace();
      }
   }
   public void play(){
      try{
         new Thread(){
            public void run(){
               clip.play();
            }
         }.start();
      }catch(Throwable e){
         e.printStackTrace();
      }
   }
}