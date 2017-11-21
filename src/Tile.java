/**
 * This class represents a graphical Tile in the game.
 * If you use it, you should EXTEND it or ADD MORE PROPERTIES
 */
public class Tile  {

	private String fileName;	// Filename should match the ImageIcon used
	
	public Tile(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}

	@Override
	public String toString() {
		return "[" + fileName + "]";
	}
}