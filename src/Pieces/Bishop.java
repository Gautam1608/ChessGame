package Pieces;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece{

	private static final ArrayList<Point> moveTypeList = new ArrayList<>();

	public Bishop(int file, int rank, boolean isWhite) {
		super(file, rank, isWhite);
		for (int i = -7; i < 9; i++) {
			moveTypeList.add(new Point(i, i));
			moveTypeList.add(new Point(-i,i));
		}
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
		return allMoves;
	}
}
