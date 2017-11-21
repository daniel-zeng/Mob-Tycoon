import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Entity {
	String[] fileName = { "", "", "", "" };
	String dir_f = "images/";
	boolean isGridPosType;
	int x, y, speed, tileSize;
	String type = "";
	Image[] i = new Image[4];
	int dir = 2;
	boolean hasFourImages = false;

	public static final int UP = 0;
	public static final int LEFT = 1;
	public static final int DOWN = 2;
	public static final int RIGHT = 3;

	MBController control = null;

	Rectangle2D.Double collision = new Rectangle2D.Double();
	Rectangle2D.Double tile_bounds = new Rectangle2D.Double();

	public Entity(int speed, String type, int tileSize, boolean hasFourImages, MBController c) {
		this.speed = speed;
		this.type = type;
		this.tileSize = tileSize;
		this.hasFourImages = hasFourImages;
		this.control = c;
	}

	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Rectangle2D.Double getCollision() {
		return collision;
	}

	public boolean isMoveable(int x, int y) {
		int width = control.getCols() * control.getTileSize();
		int height = control.getRows() * control.getTileSize();
		if (x + tileSize < width && x >= 0 && y + tileSize < height && y >= 0) {
		} else {
			return false;
		}
		int r_base = (int) ((y + tileSize * .5) / tileSize);
		int c_base = (int) ((x + tileSize * .3) / tileSize);
		int r_top = (int) ((y + tileSize * .8) / tileSize);
		int c_top = (int) ((x + tileSize * .7) / tileSize);

		return control.isWalk(r_base, c_base, false) && control.isWalk(r_base, c_top, false)
				&& control.isWalk(r_top, c_base, false) && control.isWalk(r_top, c_top, false);
	}


	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isHasFourImages() {
		return hasFourImages;
	}

	public int getDir() {
		return dir;
	}

	public void setDir(int dir) {
		this.dir = dir;
	}

	public void setHasFourImages(boolean hasFourImages) {
		this.hasFourImages = hasFourImages;
	}

	public void constructImage() {
		try {
			if (hasFourImages) {
				for (int in = 0; in < fileName.length; in++) {
					i[in] = ImageIO.read(new File(dir_f + fileName[in]));
				}
			} else {
				i[0] = ImageIO.read(new File(dir_f + fileName[0]));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Image getI(int in) {
		return i[in];
	}

	public String[] getFileName() {
		return fileName;
	}

	public String getFileName(int i) {
		return fileName[i];
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setControl(MBController control) {
		this.control = control;
	}

	public MBController getControl() {
		return control;
	}

	public void setFileNameMult(String[] fileName) {
		this.fileName = fileName;
	}

	//only sets the first one
	public void setFileNameSing(String fileName) {
		this.fileName[0] = fileName;
	}

}
