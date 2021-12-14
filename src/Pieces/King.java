package Pieces;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class King extends Piece {

	private static final ArrayList<Point> moveTypeList = new ArrayList<>(Arrays.asList(
			new Point(0, 1), new Point(1, 1), new Point(1, 0), new Point(1, -1),
			new Point(0, -1), new Point(-1, 1), new Point(-1, 0), new Point(-1, -1)
	));

	public King(int file, int rank, boolean isWhite) {
		super(file, rank, isWhite);
	}

	@Override
	public List<Point> getAllMoves() {
		ArrayList<Point> allMoves = new ArrayList<>();
		for (Point p : moveTypeList) {
			Point addedPoint;
			addedPoint = new Point(position.x + p.x, position.y + p.y);
			if (bound.contains(addedPoint)) {
				allMoves.add(addedPoint);
			}
		}
		if (position.equals(new Point(5, 1)) && isWhite) {
			allMoves.add(new Point(7, 1));
			allMoves.add(new Point(3, 1));
		} else if (position.equals(new Point(5, 8)) && !isWhite){
			allMoves.add(new Point(7, 8));
			allMoves.add(new Point(3, 8));
		}
			return allMoves;
	}
}
