package Pieces;

import java.awt.*;
import java.util.List;

public abstract class Piece {
	/**
	 *x-cord is file, y-cord is rank
	*/
	 public Point position;
	public boolean isWhite;
	/**
	 * Use bounds to check if the piece is still in the board by using bounds.contains()
	 */
	public static final Rectangle bound = new Rectangle(1,1,8,8);


	public Piece(int file, int rank, boolean isWhite) {
		this.position = new Point(file,rank);
		this.isWhite = isWhite;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Piece piece)){
			return false;
		}
		return ((isWhite == piece.isWhite) && position.equals(piece.position));
	}
	/**
	 * @return All the possible moves, which can be made on empty board.
	 */
	public abstract List<Point> getAllMoves();

}
