import java.awt.Image;
import java.awt.Toolkit;

public class Animation
{
   Image[] image;

   int current = 0;

   int delay;

   int duration;

   public Animation(String file, int count, int duration)
   {
      image = new Image[count];

      for(int i = 0; i < count; i++)
      {
         image[i] = Toolkit.getDefaultToolkit().getImage(file+i+".png");
      }

      this.duration = duration;
      delay = duration;
   }

   public Image stillImage()
   {
      return image[0];
   }

   public Image nextImage()
   {
       if(delay == 0)
       {
          current++;

          if(current == image.length)
          {
             current = 1;
          }

          delay = duration;
       }
       else
       {
          delay--;
       }

       return image[current];
   }
}







