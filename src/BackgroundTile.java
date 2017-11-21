import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Represents a scenery tile intended to be used as a background tile in the game.
 * A BackgroundTile keeps track of whether or not it can be walked on.  If it can't
 * be walked on then the tile should act as a barrier.
 */
public class BackgroundTile extends Tile {
	String dir = "images/";
	Image i = null;
	private boolean walkable;	// Determines whether this tile can be walked on
	
	public BackgroundTile(String fileName, boolean walkable) {
		super(fileName);
		this.walkable = walkable;
		constructImage();
	}
	
	public boolean getPropertyWalkable() {
		return walkable;
	}
	
	public void setPropertyWalkable(boolean newValue) {
		walkable = newValue;
	}
	public void constructImage() {
		try {
			i = ImageIO.read(new File(dir + getFileName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Image getI() {
		return i;
	}

	@Override
	public String toString() {
		return "[" + getFileName() + " " + walkable + "]";
	}
}