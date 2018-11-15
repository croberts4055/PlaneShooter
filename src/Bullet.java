import javax.swing.ImageIcon;
import java.awt.*;



public class Bullet {

	int x;
	int y;
	int w = 28; // width of bullet image
	int h = 29; // height of bullet image

	Image image;
	boolean visible;

	public Bullet(int x, int y) {

		this.x = x;
		this.y = y;
		ImageIcon newBullet = new ImageIcon("img/Bullet.png");
		image = newBullet.getImage(); 
		visible = true;		// When a bullet is first created, it is visible.
	}

	// Getter methods in case I need to access variables of the bullet in another class.
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Rectangle getBounds() {

		return new Rectangle(x,y,w,h);
	}

	public boolean getVisible() {
		return visible;
	}

	public Image getImage() {
		return image;
	}


	public void move() {
		x += 12; // How fast the bullets move.

		if (x > Game.WIDTH) // if the bullet goes off screen, make it invisible
		{
			visible = false;
		}


	}
}
