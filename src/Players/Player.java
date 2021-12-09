package Players;

import Pieces.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Player has properties of all the pieces for the same color.
 */
public class Player {
	public final boolean isWhite;
	public List<Piece> allPieces = new ArrayList<>();

	public Player(boolean isWhite) {
		this.isWhite = isWhite;
		int rank = isWhite?2:7;
		for (int file = 1; file <= 8; file++) {
			allPieces.add(new Pawn(file, rank, isWhite));
		}
		rank = isWhite?1:8;
		allPieces.add(new King(5, rank, isWhite));
		allPieces.add(new Knight(2,rank,isWhite));
		allPieces.add(new Knight(7,rank,isWhite));
		allPieces.add(new Rook(1,rank,isWhite));
		allPieces.add(new Rook(8,rank,isWhite));
		allPieces.add(new Bishop(3,rank,isWhite));
		allPieces.add(new Bishop(6,rank,isWhite));
		allPieces.add(new Queen(4,rank,isWhite));
	}
}
