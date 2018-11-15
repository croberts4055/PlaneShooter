import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;



// Extension of the Rect class to make my own colored box targets with numbers in them. 
public class NumberedRect extends Rect {

	boolean alive;
	Random rand = new Random();
	int boxCount = rand.nextInt(60) + 5; // random number between 5 and 60;
	static Font boxLivesLeft = new Font("Arial", Font.BOLD, 24);

	public NumberedRect(double x, double y, int w, int h) {
		super(x, y, w, h);	
	}

	public Rectangle getBounds() {
		return new Rectangle((int)x,(int)y,w,h);
	}

	public boolean isAlive() {
		return alive;
	}

	public void draw(Graphics g)
	{
		if (boxCount >  0) // If the boxes number is greater than 0, then it is alive, so draw the box.
		{
			alive = true;
			g.setColor(Color.DARK_GRAY);
			g.fillRect((int)x, (int)y, w, h);
			g.setFont(boxLivesLeft);
			g.setColor(Color.RED); 
			g.drawString("" + boxCount,(int) x + 40, (int) y + 50);
		}

		else {
			alive = false; // If boxes number is 0, then it is dead.
		}


	}


}
