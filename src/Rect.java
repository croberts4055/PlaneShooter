import java.awt.Graphics;

public class Rect
{
   double x;
   double y;

   int w;
   int h;

   double vx = 0;
   double vy = 0;

   static final double g = .1;

   double ax = 0;
   double ay = 0;

   boolean held = false;

   public Rect(double x, double y, int w, int h)
   {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
   }

   public void setLocation(double x, double y)
   {
      this.x = x;
      this.y = y;
   }

   public void grab()
   {
      held = true;
   }

   public void drop()
   {
      held = false;
   }

   public boolean overlaps(Rect r)
   {
      return (x      < r.x + r.w )   &&
             (x + w  > r.x       )   &&
             (y      < r.y + r.h )   &&
             (y + h  > r.y       );
   }

   public boolean contains(int mx, int my)
   {
      return (mx > x) && (mx < x+w) && (my > y) && (my < y+h);
   }


   public void draw(Graphics g)
   {
      g.drawRect((int)x, (int)y, w, h);
   }

   public void setAcceleration(double ax, double ay)
   {
      this.ax = ax;
      this.ay = ay;
   }

   public void setVelocity(double vx, double vy)
   {
      this.vx = vx;
      this.vy = vy;
   }
   
   public void moveLeftBy(int dx)
	{
		x -= dx;
	}

   public void move()
   {
      vx += ax;
      vy += ay;
      x += vx;
      y += vy;
   }


   public void moveBy(double dx, double dy)
   {
      x += dx;
      y += dy;
   }

   public void resizeBy(int dw, int dh)
   {
       w += dw;
       h += dh;
   }


}