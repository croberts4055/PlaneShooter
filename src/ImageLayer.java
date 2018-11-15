import java.awt.*;
public class ImageLayer
{
   Image image;

   int x;
   int y;
   int z;


   public ImageLayer(String file, int x, int y, int z)
   {
      image = Toolkit.getDefaultToolkit().getImage(file);

      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void moveLeftBy(int dx)
   {
      x -= dx;
   }

   public void moveRightBy(int dx)
   {
      x += dx;
   }


   public void draw(Graphics g)
   {
      for(int i = 0; i < 300; i++)

          g.drawImage(image, x/z + 1024*i, y, null);

   }

}