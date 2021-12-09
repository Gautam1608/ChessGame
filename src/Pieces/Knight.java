package Pieces;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Knight extends Piece{

	private static final ArrayList<Point> moveTypeList = new ArrayList<>(Arrays.asList(
			new Point(1, 2), new Point(2, 1), new Point(2, -1), new Point(1, -2),
			new Point(-1, 2), new Point(-2, 1), new Point(-2, -1), new Point(-1, -2)
	));

	public Knight(int file, int rank, boolean isWhite) {
		super(file, rank, isWhite);
	}

	@Override
	public List<Point> getAllMoves() {
		ArrayList<Point> allMoves = new ArrayList<>();
		for(Point p : moveTypeList){
			Point addedPoint;
			addedPoint = new Point(position.x+p.x, position.y+p.y);
			if (bound.contains(addedPoint)) {
				allMoves.add(addedPoint);
			}
		}
		return allMoves;
	}
}
