import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.Timer;

public class NPC extends Char {
	public static final int EASY = 0;
	public static final int MEDIUM = 1;
	public static final int HARD = 2;

	int diffLevel = 0;
	int delay = 1000;
	Timer t;

	int angle = 0;
	
	int npcHitDmg = 0;
	
	public NPC(int diffLevel, int x, int y, int tileSize, MBController c) {
		super(x, y, 0, "npc", tileSize, true, c);
		this.diffLevel = diffLevel;
		String[] strs = { "npc_back.png", "npc_left.png", "npc_front.png", "npc_right.png" };
		setFileNameMult(strs);
		constructImage();

		switch (diffLevel) {
		case EASY:
			delay = 400;
			setSpeed(6);
			npcHitDmg = 5;
			break;
		case MEDIUM:
			delay = 270;
			setSpeed(8);
			npcHitDmg = 8;
			break;
		case HARD:
			delay = 150;
			setSpeed(10);
			npcHitDmg = 11;
			break;
		}

		t = new Timer(delay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeDir();
			}
		});
		t.start();

	}

	public void startT() {
		t.start();
	}

	public void changeDir() {
		int range = 180;
		Random rand = new Random();
		int newA = rand.nextInt(range);
		newA -= range / 2;
		angle = (angle + newA) % 360;
	}

	public void move() {
		int newY = getY();
		int newX = getX();
		int oldY = newY;
		int oldX = newX;

		double rad = Math.toRadians(angle);
		int dX = (int) (Math.cos(rad) * speed);
		int dY = (int) (Math.sin(rad) * speed);
		newX += dX;
		newY += dY;
		int tet = angle - 45;
		if (tet < 0)
			tet += 360;
		if (tet == 360)
			tet = 0;
		int image_dir = tet / 90;
		if (image_dir == 0) {
			image_dir = 2;
		} else if (image_dir == 2) {
			image_dir = 0;

		}
		setDir(image_dir);
		if (isMoveable(newX, newY)) {
			setPos(newX, newY);
		} else if (isMoveable(newX, oldY)) {
			setPos(newX, oldY);
		} else if (isMoveable(oldX, newY)) {
			setPos(oldX, newY);
		}

	}
	public int getHitDmg() {
		return npcHitDmg;
	}
	public int getDiffLevel() {
		return diffLevel;
	}

	public void setDiffLevel(int diffLevel) {
		this.diffLevel = diffLevel;
	}

}
