import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class MBView {

	public static final int UP = 0;
	public static final int LEFT = 1;
	public static final int DOWN = 2;
	public static final int RIGHT = 3;

	public static final int TITLE = 0;
	public static final int GAME = 1;
	public static final int MENU = 2;
	public static final int EMPTY = 3;

	MBController control = null;
	ViewingPanel view;
	VidPlayer vid = new VidPlayer();

	JFrame window;
	JPanel title;

	/**
	 * Menu Items
	 */
	InGameMenu menu;
	InGameMenu upgradePane;
	JButton upgrades;
	JButton quit;
	JButton exitup;

	JButton healthUp, shootUp, regenUp, dmgUp, moveUp;

	Timer visual_t, mech_t;
	TimerTask refreshFPS;

	double refresh = 60; //fps cap
	double fps = 0;
	int frames = 0;

	int pic_length, winL, winH, size;
	int mX, mY;
	boolean shoot = false;
	int shootCount = 0;

	Random ran = new Random();

	boolean[] keys = new boolean[4];

	int secs = 0;
	boolean gameRunning;

	public MBView(MBController c, int size, int winL, int winH) {
		this.control = c;
		this.winH = winH;
		this.winL = winL;
		this.size = size;


		window = new JFrame("Mob Tycoon");
		window.setBounds(0, 0, winL, winH);
		window.setResizable(false);
		window.setUndecorated(true);

		window.setLayout(null);

		window.addMouseListener(new DownListen());
		window.addMouseMotionListener(new DragListen());

		/**
		 * Prepare sizes for the view
		 * 
		 * @size is the smallest dimension of the monitor - either width or
		 *       height
		 */
		pic_length = c.getTileSize();

		/**
		 * Menu
		 */

		//menu label!
		JPanel spacer;

		menu = new InGameMenu();
		int wMenu = Math.max(800, (int) (winL * .30));
		int hMenu = Math.max(300, (int) (winH * .30));
		int xMenu = winL/2 - wMenu/2;
		int yMenu = winH/2 - hMenu/2;
		menu.setBounds(xMenu, yMenu, wMenu, hMenu);
		menu.setUndecorated(true);
		menu.setLayout(new FlowLayout());

		JLabel menuLabel = new JLabel();
		menuLabel.setText("Menu");
		menu.add(menuLabel);
		spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(winL, 0));
		menu.add(spacer);

		upgrades = new JButton("Upgrades");

		quit = new JButton("Quit Game");
		quit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}

		});

		menu.add(upgrades);
		spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(winL, 0));
		menu.add(spacer);
		menu.add(quit);


		upgradePane = new InGameMenu();
		upgradePane.setBounds(xMenu, yMenu, wMenu, hMenu);
		upgradePane.setUndecorated(true);
		upgradePane.setLayout(new FlowLayout());

		JLabel upLabel = new JLabel();
		upLabel.setText("Upgrades");
		upgradePane.add(upLabel);

		spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(winL, 0));
		upgradePane.add(spacer);

		exitup = new JButton("Exit Upgrades");
		exitup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				menu.setVisible(true);
				upgradePane.setVisible(false);
			}

		});

		healthUp = new JButton("Upgrade Health");
		upgradePane.add(healthUp);
		JLabel healthT = new JLabel();
		healthT.setText("Current Level: 1, Cost to Upgrade: " + control.getCosts(0));
		healthUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (control.upgradePlayer(0)) {
					healthT.setText("Current Level: " + control.getPlayer().getLevel(0) + ", Cost to Upgrade: "
							+ control.getCosts(0));
					setButtonAvail();
				}
			}

		});
		upgradePane.add(healthT);
		spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(winL, 0));
		upgradePane.add(spacer);

		shootUp = new JButton("Upgrade Shooting Speed");
		upgradePane.add(shootUp);
		JLabel shootT = new JLabel();
		shootT.setText("Current Level: 1, Cost to Upgrade: " + control.getCosts(1));
		shootUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (control.upgradePlayer(1)) {
					shootT.setText("Current Level: " + control.getPlayer().getLevel(1) + ", Cost to Upgrade: "
							+ control.getCosts(1));
					setButtonAvail();
				}
			}

		});
		upgradePane.add(shootT);
		spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(winL, 0));
		upgradePane.add(spacer);

		regenUp = new JButton("Upgrade Health Regen");
		upgradePane.add(regenUp);
		JLabel regenT = new JLabel();
		regenT.setText("Current Level: 1, Cost to Upgrade: " + control.getCosts(2));
		regenUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (control.upgradePlayer(2)) {
					regenT.setText("Current Level: " + control.getPlayer().getLevel(2) + ", Cost to Upgrade: "
							+ control.getCosts(2));
					setButtonAvail();
				}
			}

		});
		upgradePane.add(regenT);
		spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(winL, 0));
		upgradePane.add(spacer);

		dmgUp = new JButton("Upgrade Damage");
		upgradePane.add(dmgUp);
		JLabel dmgT = new JLabel();
		dmgT.setText("Current Level: 1, Cost to Upgrade: " + control.getCosts(3));
		dmgUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (control.upgradePlayer(3)) {
					dmgT.setText("Current Level: " + control.getPlayer().getLevel(3) + ", Cost to Upgrade: "
							+ control.getCosts(3));
					setButtonAvail();
				}
			}

		});
		upgradePane.add(dmgT);
		spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(winL, 0));
		upgradePane.add(spacer);

		moveUp = new JButton("Upgrade Movement Speed");
		upgradePane.add(moveUp);
		JLabel moveT = new JLabel();
		moveT.setText("Current Level: 1, Cost to Upgrade: " + control.getCosts(4));
		moveUp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (control.upgradePlayer(4)) {
					moveT.setText("Current Level: " + control.getPlayer().getLevel(4) + ", Cost to Upgrade: "
							+ control.getCosts(4));
					setButtonAvail();
				}
			}

		});
		upgradePane.add(moveT);
		spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(winL, 5));
		upgradePane.add(spacer);

		upgradePane.add(exitup);

		upgrades.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				menu.setVisible(false);
				upgradePane.setVisible(true);
				setButtonAvail();
			}

		});

		menu.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == 'e') {
					if (menu.isVisible()) {
						menu.setVisible(false);
						gameRunning = true;
					}
				}
			}

		});

		//key strokes
		window.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				int k = e.getKeyCode();
				switch (control.getMode()) {
				case GAME:
					if (k == KeyEvent.VK_W || k == KeyEvent.VK_UP) {
						keys[UP] = false;
					}
					if (k == KeyEvent.VK_A || k == KeyEvent.VK_LEFT) {
						keys[LEFT] = false;

					}
					if (k == KeyEvent.VK_S || k == KeyEvent.VK_DOWN) {
						keys[DOWN] = false;

					}
					if (k == KeyEvent.VK_D || k == KeyEvent.VK_RIGHT) {
						keys[RIGHT] = false;

					}
					break;
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
				int k = e.getKeyCode();
				switch (control.getMode()) {
				case GAME:
					//WASD and Arrowkeys
					if (k == KeyEvent.VK_W || k == KeyEvent.VK_UP) {
						keys[UP] = true;
					}
					if (k == KeyEvent.VK_A || k == KeyEvent.VK_LEFT) {
						keys[LEFT] = true;

					}
					if (k == KeyEvent.VK_S || k == KeyEvent.VK_DOWN) {
						keys[DOWN] = true;

					}
					if (k == KeyEvent.VK_D || k == KeyEvent.VK_RIGHT) {
						keys[RIGHT] = true;

					}

					if (k == KeyEvent.VK_E) {
						if (!menu.isVisible()) {
							menu.setVisible(true);
							gameRunning = false;
						}
					}
					break;

				case TITLE:
					if (k == KeyEvent.VK_ENTER) {
						control.startGame();
					}
					break;
				}

			}
		});

		/**
		 * Title Screen
		 */
		title = new JPanel();
		JLabel titleAnimation = new JLabel();
		title.setBounds(0, 0, size, size);
		title.setBackground(Color.BLACK);
		title.add(titleAnimation);
		window.add(title);
		vid.playVid("title.gif", titleAnimation, window);
		String show = "You are the mob boss. \n" + "Kill as many enemies as you can before you die.\n"
				+ "Show the world who's the best mob boss.\n\n" + "Use WASD or Arrow Keys to move.\n"
				+ "Press E to toggle menu.\n" + "Hold down Left Mouse Button to shoot.";
		JOptionPane.showMessageDialog(null, show);


		window.setVisible(true);

		visual_t = new Timer((int) (1000 / refresh), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				update();
				if (gameRunning)
					frames++;
			}
		});
		int mechUpdateRate = 60;
		int npcChance = 150;


		mech_t = new Timer((int) (1000 / mechUpdateRate), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (menu.isVisible()) {
					menu.requestFocus();
				}
				if (upgradePane.isVisible()) {
					upgradePane.requestFocus();
				}
				if (gameRunning) {
					control.moveAll(keys);
					if (shoot) {
						shootCount++;
						int shootRate = control.getPlayer().getShootRate();
						if (shootCount % shootRate == 0) {
							control.shoot(mX, mY);
						}
					}
					if (ran.nextInt(npcChance) == 0) {
						control.newNPC();
					}
					control.checkCollisions();
				}
			}
		});
		gameRunning = true;
		refreshFPS = new TimerTask() {
			@Override
			public void run() {
				if (gameRunning) {

					control.setToRegen(true);

					fps = frames;
					frames = 0;
					secs++;
					System.out.println(fps + " FPS");
				}
			}
		};
		java.util.Timer fpsRun = new java.util.Timer();
		fpsRun.scheduleAtFixedRate(refreshFPS, 1000, 1000);

	}

	public void setButtonAvail() {
		boolean[] avail = control.getAvailButtons();
		healthUp.setEnabled(avail[0]);
		shootUp.setEnabled(avail[1]);
		regenUp.setEnabled(avail[2]);
		dmgUp.setEnabled(avail[3]);
		moveUp.setEnabled(avail[4]);
	}

	public void stopGame() {
		mech_t.stop();
		int enemies = control.getEnemiesKilled();
		String enemy_end = "";
		if (enemies == 1) {
			enemy_end = "enemy";
		} else {
			enemy_end = "enemies";
		}
		String rank = "";
		int inc = 10;
		if (enemies < inc * 1) {
			rank = "Noob";
		} else if (enemies < inc * 2) {
			rank = "Novice";
		} else if (enemies < inc * 3) {
			rank = "Expert";
		} else if (enemies < inc * 4) {
			rank = "Master";
		} else if (enemies >= inc * 4) {
			rank = "GRAND Master";
		}
		String show = "You have killed " + enemies + " " + enemy_end + ".\n" + "You are a: " + rank + " Mob Boss\n"
				+ "You have lasted for " + secs + " seconds before dying.\n" + "Restart the game to try again";
		JOptionPane.showMessageDialog(null, show);
		refreshFPS.cancel();
	}

	/**
	 * Implementing the Drawing Panel / Starting the Game
	 */
	public void startGame() {
		title.setVisible(false);
		control.setMode(GAME);
		view = new ViewingPanel();
		view.setBounds(0, 0, winL, winH);
		window.add(view);
		view.repaint();
		visual_t.start();
		mech_t.start();
	}

	public void update() {
		view.repaint();
	}

	private class DownListen implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {
			switch (control.getMode()) {
			case GAME:
				if (SwingUtilities.isLeftMouseButton(e)) {
					mX = e.getX();
					mY = e.getY();
					shoot = true;
				} else if (SwingUtilities.isRightMouseButton(e)) {
				}
				break;

			case TITLE:
				if (SwingUtilities.isLeftMouseButton(e)) {
					control.startGame();
				}
				break;
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			switch (control.getMode()) {
			case GAME:
				if (SwingUtilities.isLeftMouseButton(e)) {
					shoot = false;
				} else if (SwingUtilities.isRightMouseButton(e)) {
				}
				break;
			}
		}

	}

	private class DragListen extends MouseAdapter {
		@Override
		public void mouseDragged(MouseEvent e) {
			switch (control.getMode()) {
			case GAME:
				if (SwingUtilities.isLeftMouseButton(e)) {
					mX = e.getX();
					mY = e.getY();
				} else if (SwingUtilities.isRightMouseButton(e)) {
				}
				break;

			case TITLE:
				if (SwingUtilities.isLeftMouseButton(e)) {
					control.startGame();
				}
				break;
			}
		}

	}

	private class ViewingPanel extends JPanel {
		String dir = "images/";

		static final long serialVersionUID = 1234567890L;

		public void paintComponent(Graphics g) {
			g.setColor(Color.black);
			g.fillRect(0, 0, size, size);

			//HEALTH BAR
			g.setColor(Color.RED);

			int barWidth = (int) (control.getPlayer().hpPercent() * (size / 2));
			g.fillRect(size / 60, size / 2 + size / 60, barWidth, size / 60);
			g.drawRect(size / 60, size / 2 + size / 60, size / 2, size / 60);
			g.setColor(Color.WHITE);
			g.drawString("Health", size / 60, size / 2 + size / 60);
			int cash = control.getPlayer().getCash();
			g.drawString("Cash: " + cash, size / 60, size / 2 + size / 20);

			//draws the tiles
			int rows = control.getRows();
			int cols = control.getCols();
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					g.drawImage(control.getBTImage(r, c), c * pic_length, r * pic_length, pic_length, pic_length, null);
				}
			}

			//visual debug
			g.setColor(Color.red);
			boolean drawBoundingBox = false;
			boolean drawSubBoundingBox = false;
			boolean drawCollisionBox = false;

			//draws the entities
			ArrayList<Char> chars = control.getChars();
			for (Char c : chars) {
				int x = c.getX();
				int y = c.getY();

				g.drawImage(c.getI(c.getDir()), x, y, pic_length, pic_length, null);

				if (drawBoundingBox) {
					g.drawLine(x, y, x + pic_length, y);
					g.drawLine(x, y, x, y + pic_length);
					g.drawLine(x, y + pic_length, x + pic_length, y + pic_length);
					g.drawLine(x + pic_length, y, x + pic_length, y + pic_length);
				}
				if (drawSubBoundingBox) {
					int r_base = (int) (y + pic_length * .5);
					int c_base = (int) (x + pic_length * .3);
					int r_top = (int) (y + pic_length * .8);
					int c_top = (int) (x + pic_length * .7);
					g.drawLine(c_base, r_base, c_base, r_top);
					g.drawLine(c_base, r_base, c_top, r_base);
					g.drawLine(c_base, r_top, c_top, r_top);
					g.drawLine(c_top, r_base, c_top, r_top);
				}
				if (drawCollisionBox) {
					g.setColor(Color.green);
					Rectangle2D.Double a = c.getCollision();
					int c_base = (int) (a.getX());
					int r_base = (int) (a.getY());
					int c_top = (int) (a.getX() + a.getWidth());
					int r_top = (int) (a.getY() + a.getHeight());
					g.drawLine(c_base, r_base, c_base, r_top);
					g.drawLine(c_base, r_base, c_top, r_base);
					g.drawLine(c_base, r_top, c_top, r_top);
					g.drawLine(c_top, r_base, c_top, r_top);
				}

			}
			g.setColor(Color.red);
			ArrayList<Bullet> buls = control.getBullets();
			for (Bullet c : buls) {
				int x = c.getX();
				int y = c.getY();

				g.drawImage(c.getI(c.getDir()), x, y, pic_length, pic_length, null);

				if (drawBoundingBox) {
					g.drawLine(x, y, x + pic_length, y);
					g.drawLine(x, y, x, y + pic_length);
					g.drawLine(x, y + pic_length, x + pic_length, y + pic_length);
					g.drawLine(x + pic_length, y, x + pic_length, y + pic_length);
				}
				if (drawSubBoundingBox) {
					int r_base = (int) (y + pic_length * .5);
					int c_base = (int) (x + pic_length * .3);
					int r_top = (int) (y + pic_length * .8);
					int c_top = (int) (x + pic_length * .7);
					g.drawLine(c_base, r_base, c_base, r_top);
					g.drawLine(c_base, r_base, c_top, r_base);
					g.drawLine(c_base, r_top, c_top, r_top);
					g.drawLine(c_top, r_base, c_top, r_top);
				}
				if (drawCollisionBox) {
					g.setColor(Color.green);
					Rectangle2D.Double a = c.getCollision();
					int c_base = (int) (a.getX());
					int r_base = (int) (a.getY());
					int c_top = (int) (a.getX() + a.getWidth());
					int r_top = (int) (a.getY() + a.getHeight());
					g.drawLine(c_base, r_base, c_base, r_top);
					g.drawLine(c_base, r_base, c_top, r_base);
					g.drawLine(c_base, r_top, c_top, r_top);
					g.drawLine(c_top, r_base, c_top, r_top);
				}
			}
			boolean drawLines = false;
			g.setColor(Color.lightGray);
			if (drawLines) {
				for (int c = 0; c < this.getWidth(); c += pic_length)
					g.drawLine(c, 0, c, this.getHeight());

				for (int r = 0; r < this.getHeight(); r += pic_length)
					g.drawLine(0, r, this.getWidth(), r);

			}

		}

	}

	private class VidPlayer {

		public void playVid(String vid, JLabel l, JFrame f) {
			ImageIcon icon = new ImageIcon(vid);
			l.setIcon(icon);
			f.repaint();
		}
	}

	private class InGameMenu extends JDialog {

		private static final long serialVersionUID = 1L;

		public void paintComponent(Graphics g) {
			g.setColor(Color.BLACK);
			g.drawString("Menu", 20, 20);
		}

	}

	private class TextBox extends JDialog {

		private static final long serialVersionUID = 1L;

	}

}

