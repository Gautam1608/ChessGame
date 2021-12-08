package Pieces;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Pawn has the move list and position for each pawn.
 */
public class Pawn extends Piece {

	private static final ArrayList<Point> moveTypeList = new ArrayList<>(Arrays.asList(
			new Point(0, 1), new Point(1, 1), new Point(-1, 1)
	));

	public Pawn(int file, int rank, boolean isWhite) {
		super(file, rank, isWhite);
	}

	@Override
	public List<Point> getAllMoves() {
		ArrayList<Point> allMoves = new ArrayList<>();
		for(Point p : moveTypeList){
			Point addedPoint;
			if(isWhite) {
				addedPoint = new Point(position.x + p.x, position.y + p.y);
			}
			else {
				addedPoint = new Point(position.x + p.x, position.y - p.y);
			}
			if (bound.contains(addedPoint)) {
				allMoves.add(addedPoint);
			}
		}
		if((position.y==2 && isWhite)){
			allMoves.add(new Point(position.x,position.y+2));
		}
		else if((position.y==7 && !isWhite)){
			allMoves.add(new Point(position.x,position.y-2));
		}
		return allMoves;
	}
}
