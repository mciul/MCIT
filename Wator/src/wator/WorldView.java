/**
 * 
 */
package wator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

/**
 * @author Student
 * 
 */
public class WorldView extends JPanel {
	WatorModel model;
	double horizontalScale;
	double verticalScale;
	int hSize;
	int vSize;
	final private ArrayList<Loc> updatedLocs = new ArrayList<Loc>();

	public WorldView(WatorModel model, int initialCanvasWidth,
			int initialCanvasHeight) {
		this.model = model;
		horizontalScale = 1.0 * initialCanvasWidth / model.getWidth();
		verticalScale = 1.0 * initialCanvasHeight / model.getHeight();
		hSize = (int) horizontalScale;
		vSize = (int) verticalScale;
		if (hSize < 0) {
			hSize = 0;
		}
		if (vSize < 0) {
			vSize = 0;
		}
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public synchronized void paint(Graphics g) {
		// g.setColor(Color.BLACK);
		// g.fillRect(0, 0, (int)(horizontalScale * model.getWidth()),
		// (int)(verticalScale * model.getHeight()));
		for (int y = 0; y < model.getHeight(); y++) {
			for (int x = 0; x < model.getWidth(); x++) {
				paintLoc(g, model.getLoc(x, y));
			}
		}
	}

	/**
	 * Draw the occupant of one location
	 * 
	 * @param loc
	 */
	private void paintLoc(Graphics g, Loc loc) {
		Pisces occupant = loc.getOccupant();
		if (occupant == null) {
			g.setColor(Color.BLACK);
		} else if (occupant.isEdible()) {
			g.setColor(Color.GREEN);
		} else {
			g.setColor(Color.RED);
		}
		int x = (int) (horizontalScale * loc.getX());
		int y = (int) (verticalScale * loc.getY());
		g.fillRect(x, y, hSize, vSize);
	}
}
