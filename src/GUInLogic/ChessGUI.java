package GUInLogic;

import Exceptions.InvalidMoveException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Phani Gautam on 4th December 2021.
 */

public class ChessGUI {

	public static void main(String[] args) {
		new ChessGUI();
	}

	/**
	 * Set theme to either GRAY or GREEN(for now)
	 **/
	private final Color[] theme = Theme.GRAY.getColors();

	//Board Constants
	/**
	 * Length of each side of the Grid
	 **/
	public final int GRID_SIZE = 64;
	/**
	 * Root of number of squares on the board
	 */
	public final int GRID_NUMBER = 8;

	public ChessGUI() {
		//EventQueue is the main thread swing runs on.
		EventQueue.invokeLater(() -> {
			//Frame settings
			JFrame frame = new JFrame("Chess");
			frame.setLayout(new BorderLayout());
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setResizable(false);//cannot resize the board
			frame.add(new BoardPanel());
			frame.pack(); // fits the frame to the panel
			frame.setVisible(true);
			frame.setLocationRelativeTo(null); // sets the frame to the center of the screen
		});
	}

	/**
	 * Enum with different colors, has GRAY and GREEN (for now)
	 */
	public enum Theme {
		GRAY(new Color[]{new Color(168, 168, 168), new Color(136, 136, 136), new Color(77, 110, 82)}),
		GREEN(new Color[]{new Color(240, 240, 200), new Color(113, 136, 87), new Color(73, 153, 141)});

		private final Color[] colors;

		Theme(Color[] colors) {
			this.colors = colors;
		}

		public Color[] getColors() {
			return colors;
		}
	}

	public class BoardPanel extends JPanel {

		private final BufferedImage boardPanel;
		private final Rectangle boardRectangle = new Rectangle(new Point(), new Dimension(GRID_SIZE * GRID_NUMBER, GRID_SIZE * GRID_NUMBER));
		//Uses the setHighLightCell to highlight the cell
		private Point highLightCell;


		public BoardPanel() {
			int width = GRID_SIZE * GRID_NUMBER;
			int height = GRID_SIZE * GRID_NUMBER;
			boolean isWhite = true;
			setLayout(new BoardLayoutManager());
			boardPanel = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = boardPanel.createGraphics();
			//Loop for setting the dark and light squares
			for (int row = 0; row < GRID_NUMBER; row++) {
				for (int col = 0; col < GRID_NUMBER; col++) {
					if (isWhite) {
						g2d.setColor(theme[0]);
					} else {
						g2d.setColor(theme[1]);
					}
					g2d.fill(new Rectangle(row * GRID_SIZE, col * GRID_SIZE, GRID_SIZE, GRID_SIZE));
					isWhite = !isWhite;
				}
				isWhite = !isWhite;
			}
			final int imageD = 200;//root of number of pixels of each image in chessArt.png
			BufferedImage chessArt = null;
			try {
				chessArt = ImageIO.read(new File("resources/chessArt.png"));
			} catch (IOException e) {
				System.out.println("Did not find image of Chess Men");
			}
			int x = 0, y = 0;//coordinates for the pieces
			// loop to check which piece should fit where and adds a label for each piece
			for (int file = 1; file <= GRID_NUMBER; file++) {
				for (int rank = 1; rank <= GRID_NUMBER; rank++) {
					if (rank == 8 && (file == 1 || file == 8)) {
						x = 4;
						y = 0;
					} else if (rank == 8 && (file == 2 || file == 7)) {
						x = 3;
						y = 0;
					} else if (rank == 8 && (file == 3 || file == 6)) {
						x = 2;
						y = 0;
					} else if (rank == 8 && file == 4) {
						x = 1;
						y = 0;
					} else if (rank == 8) {
						x = 0;
						y = 0;
					} else if (rank == 7) {
						x = 5;
						y = 0;
					}


					if (rank == 1 && (file == 1 || file == 8)) {
						x = 4;
						y = 1;
					} else if (rank == 1 && (file == 2 || file == 7)) {
						x = 3;
						y = 1;
					} else if (rank == 1 && (file == 3 || file == 6)) {
						x = 2;
						y = 1;
					} else if (rank == 1 && file == 4) {
						x = 1;
						y = 1;
					} else if (rank == 1) {
						x = 0;
						y = 1;
					} else if (rank == 2) {
						x = 5;
						y = 1;
					}
					JLabel piece = new JLabel();
					assert chessArt != null;
					piece.setIcon(new ImageIcon(chessArt.getSubimage(imageD * x, imageD * y, imageD, imageD).getScaledInstance(64, 64, BufferedImage.SCALE_SMOOTH)));
					if (rank == 1 || rank == 2 || rank == 7 || rank == 8) {
						add(piece, new Point(file - 1, rank - 1));
					}
				}
			}
			MouseHandler mouseHandler = new MouseHandler(this);
			addMouseListener(mouseHandler);
			addMouseMotionListener(mouseHandler);
		}

		/**
		 * Converts the grid point to actual point.
		 *
		 * @param grid Takes a point between (0,0) to (7,7)
		 * @return The pixel coordinates
		 */
		public Point gridToPoint(Point grid) {
			Point p = new Point();
			if (grid != null) {
				p.x = grid.x * GRID_SIZE;
				p.y = grid.y * GRID_SIZE;
			}
			return p;
		}

		/**
		 * Converts actual point to grid point
		 *
		 * @param p Takes the actual point between
		 *          (0,0) to (GRID_SIZE*GRID_NUMBER,GRID_SIZE*GRID_NUMBER)
		 * @return The cell number in the grid
		 */
		public Point pointToGrid(Point p) {
			Point grid = null;
			if (boardRectangle.contains(p)) {
				grid = new Point();
				grid.x = p.x / GRID_SIZE;
				grid.y = p.y / GRID_SIZE;
			}
			return grid;
		}

		/**
		 * Sets the piece and repaints
		 *
		 * @param comp Get the component from the listener
		 * @param grid Give the cell coordinates
		 */
		public void setPieceGrid(Component comp, Point grid) {
			((BoardLayoutManager) getLayout()).setPieceGrid(comp, grid);
			invalidate();
			revalidate();
			repaint();
		}

		public void killEnPassantPieceGrid(Component comp, Point end,Point enPassantKill) {
			for (Component c : getComponents()) {
				Point killPoint = pointToGrid(c.getLocation());
				if (killPoint != null) {
					if (killPoint.equals(enPassantKill)) {
						remove(c);
					}
				}
			}
			revalidate();
			repaint();
			add(comp, end);
			revalidate();
			repaint();
		}

		public void killPieceGrid(Component comp, Point end) {
			for (Component c : getComponents()) {
				Point killPoint = pointToGrid(c.getLocation());
				if (killPoint != null) {
					if (killPoint.equals(end)) {
						remove(c);
					}
				}
			}
			revalidate();
			repaint();
			add(comp, end);
			revalidate();
			repaint();
		}

		public void pieceMovement(Component dragComponent, Point start, Point end) {

			int moveType;

			try {
				moveType = ChessLogic.isMoveType(start, end);
				setPieceGrid(dragComponent, end);
				//System.out.println(moveType);
				if (moveType == 1) {
					killPieceGrid(dragComponent, end);
				} else if (moveType == 2) {
					killEnPassantPieceGrid(dragComponent, end, new Point(end.x, start.y));
				}
			}
			//drops the piece back to the original square if it's a wrong move.
			catch (NullPointerException | InvalidMoveException ex) {
				//ex.printStackTrace();
				setPieceGrid(dragComponent, start);
			}
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(GRID_SIZE * GRID_NUMBER, GRID_SIZE * GRID_NUMBER);
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			Point p = new Point();
			g2d.drawImage(boardPanel, p.x, p.y, this);
			//highlights the cell if true
			if (highLightCell != null) {
				Point cell = gridToPoint(highLightCell);
				Rectangle bounds = new Rectangle(cell.x, cell.y, GRID_SIZE, GRID_SIZE);
				g2d.setColor(theme[2]);
				g2d.fill(bounds);

			}
			g2d.dispose();
		}

		public void setHighLightCell(Point p) {
			if (highLightCell != p) {
				highLightCell = p;
				repaint();
			}
		}
	}

	public class MouseHandler extends MouseAdapter {
		private Component dragComponent;
		private final BoardPanel boardPanel;
		private Point dragOffset;
		//The initial clicked point where the piece is picked from
		private Point clickedPoint = null;
		//To check if piece is being picked up or put down
		private boolean pieceClicked = false;


		public MouseHandler(BoardPanel boardPanel) {
			this.boardPanel = boardPanel;
		}

		public BoardPanel getBoardPanel() {
			return boardPanel;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			Component comp = getBoardPanel().getComponentAt(e.getPoint());
			if (comp != null && !(comp instanceof BoardPanel) && SwingUtilities.isLeftMouseButton(e) && !pieceClicked) {
				dragComponent = comp;
				dragOffset = new Point();
				dragOffset.x = e.getPoint().x - comp.getX();
				dragOffset.y = e.getPoint().y - comp.getY();
				clickedPoint = e.getPoint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (dragComponent != null) {
				//Checks if the player wants to drag or click
				if (!pieceClicked && (e.getPoint().equals(clickedPoint))) {
					pieceClicked = true;
					boardPanel.setHighLightCell(boardPanel.pointToGrid(e.getPoint()));
				}
				//else if for when the player finishes moving the piece by either clicking or dragging,
				// it's the same logic.
				else {
					Point start = boardPanel.pointToGrid(clickedPoint);
					Point end = boardPanel.pointToGrid(e.getPoint());
					BoardPanel boardPanel = getBoardPanel();
					boardPanel.pieceMovement(dragComponent,start, end);
					boardPanel.setHighLightCell(null);
					pieceClicked = false;
					dragComponent = null;
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (dragComponent != null) {
				BoardPanel boardPanel = getBoardPanel();
				Point grid = boardPanel.pointToGrid(e.getPoint());
				Point dragPoint = new Point();
				dragPoint.x = e.getPoint().x - dragOffset.x;
				dragPoint.y = e.getPoint().y - dragOffset.y;
				dragComponent.setLocation(dragPoint);
				boardPanel.setHighLightCell(grid);
			}
		}
	}

	/**
	 * I don't understand this at all,
	 * I just copied from it all from stackoverflow ;P
	 */
	 //Link :
	 //https://stackoverflow.com/questions/13698217/how-to-make-draggable-components-with-imageicon
	 //Credit to user MadProgrammer
	public class BoardLayoutManager implements LayoutManager2 {

		private final Map<Component, Point> mapGrid;

		public BoardLayoutManager() {
			mapGrid = new HashMap<>(25);
		}

		public void setPieceGrid(Component comp, Point grid) {
			mapGrid.put(comp, grid);
		}

		@Override
		public void addLayoutComponent(Component comp, Object constraints) {
			if (constraints instanceof Point) {
				mapGrid.put(comp, (Point) constraints);
			} else {
				throw new IllegalArgumentException("Unexpected constraints, expected java.awt.Point, got " + constraints);
			}
		}

		@Override
		public Dimension maximumLayoutSize(Container target) {
			return new Dimension(GRID_SIZE * GRID_NUMBER, GRID_SIZE * GRID_NUMBER);
		}

		@Override
		public float getLayoutAlignmentX(Container target) {
			return 0.5f;
		}

		@Override
		public float getLayoutAlignmentY(Container target) {
			return 0.5f;
		}

		@Override
		public void invalidateLayout(Container target) {

		}

		@Override
		public void addLayoutComponent(String name, Component comp) {

		}

		@Override
		public void removeLayoutComponent(Component comp) {
			mapGrid.remove(comp);
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			return new Dimension(GRID_SIZE * GRID_NUMBER, GRID_SIZE * GRID_NUMBER);
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(GRID_SIZE * GRID_NUMBER, GRID_SIZE * GRID_NUMBER);
		}

		@Override
		public void layoutContainer(Container parent) {
			for (Component comp : parent.getComponents()) {
				Point p = mapGrid.get(comp);
				if (p == null) {
					comp.setBounds(0, 0, 0, 0); // Remove from sight :P
				} else {
					int x = p.x * GRID_SIZE;
					int y = p.y * GRID_SIZE;
					comp.setBounds(x, y, GRID_SIZE, GRID_SIZE);
				}
			}
		}
	}
}