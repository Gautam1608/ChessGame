package Players;

import Pieces.Pawn;
import Pieces.Piece;

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
	}
}
