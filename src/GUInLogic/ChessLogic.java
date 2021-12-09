package GUInLogic;

import Exceptions.InvalidMoveException;
import Pieces.*;
import Players.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChessLogic {

	//Attributes

	//Throughout the class start and end are points where the piece is picked and dropped from
	//If either is null, null pointer exception is caught in the gui class, so no need to deal with the same
	//Also remember point.x is the file, point.y is the rank

	private static boolean isWhiteTurn = true;
	private static int turnNumber = 1;//turnNumber increases for every move made by both players

	//Left this in to be removed at the end. Helpful for testing and stuff
	private final static Player whitePlayer = new Player(true);
	private final static Player blackPlayer = new Player(false);
	private final static List<Player> bothPlayers = new ArrayList<>(Arrays.asList(whitePlayer, blackPlayer));

	//Make this null before every return statement
	private static Piece pieceMoved = null;
	//Make this false before the kill return statement
	private static boolean isPieceKill = false;
	//En passant constants
	private static Pawn enPassantPawn = null;
	//Make this false before the en passant return statement
	private static boolean isEnPassantKill = false;

	//Methods

	//Note only move piece and kill should change the values or add and delete values for the pieces, every other method should simply check the values

	/**
	 * @return The board coordinates
	 */
	private static Point correctCoordinates(Point p) {
		return new Point((p.x) + 1, (p.y ^ 7) + 1);
	}

	private static void printMoves(Point start, Point end) {
		char s = (char) ('a' + start.x - 1);
		char e = (char) ('a' + end.x - 1);
		if (!isWhiteTurn) {
			System.out.println(s + "" + start.y + " to " + e + end.y);
		} else {
			System.out.print(turnNumber + "." + s + "" + start.y + " to " + e + end.y + "  ");
		}
	}

	/**
	 * @return Null if no Piece is found
	 */
	private static Piece getPieceAtPosition(Point p, boolean isWhite) {
		int player = isWhite ? 0 : 1;
		for (Piece piece : bothPlayers.get(player).allPieces) {
			if (piece.position.equals(p)) {
				return piece;
			}
		}
		return null;
	}

	/**
	 * Call this method and then check the result using enPassantKill
	 *
	 * @param end it is the final position of the pawn
	 */
	private static void checkEnPassantKill(Point end) {
		if (enPassantPawn != null) {
			isEnPassantKill = enPassantPawn.position.equals(new Point(end.x, pieceMoved.position.y));
		}
	}

	/**
	 * @return true if the Pawn move is legal and false if not
	 */
	private static boolean checkPawnMove(Point start, Point end) {

		//Use player+forOpp as the index to get the opposite player from bothPlayers
		int player = isWhiteTurn ? 0 : 1;
		int forOpp = isWhiteTurn ? 1 : -1;

		if (forOpp * (end.y - start.y) == 2) {
			enPassantPawn = (Pawn) pieceMoved;
			for (int i = 0; i < 2; i++) {
				for (Piece block : bothPlayers.get(i).allPieces) {
					if (block.position.equals(new Point(start.x, start.y + forOpp))) {
						return false;
					}
				}
			}
			//System.out.println(enPassantPawn);
		}

		//this checks if the pawn kills something and returns true if so.
		if (end.x - start.x != 0) {
			checkEnPassantKill(end);
			if (isEnPassantKill) {
				return true;
			}
			for (Piece oppPiece : bothPlayers.get(player + forOpp).allPieces) {
				if (oppPiece.position.equals(end)) {
					return true;
				}
			}
			return false;
		}
		//this checks if the pawn is moving straight into enemy piece
		//as end.x-start.x != 0 is already checked, we don't need conditional statements here
		for (Piece oppPiece : bothPlayers.get(player + forOpp).allPieces) {
			if (oppPiece.position.equals(end)) {
				return true;
			}
		}
		return true;
	}

	/**
	 * @return true is there is a piece in-between the start and end point line
	 */

	private static boolean checkPieceInLine(Point start, Point end) {
		List<Point> blockPoints = new ArrayList<>();
		if (start.x == end.x) {
			if (start.y < end.y) {
				for (int y = start.y + 1; y <= end.y - 1; y++) {
					blockPoints.add(new Point(start.x, y));
				}
			} else if (start.y > end.y) {
				for (int y = start.y - 1; y >= end.y + 1; y--) {
					blockPoints.add(new Point(start.x, y));
				}
			}
		} else if (start.y == end.y) {
			if (start.x < end.x) {
				for (int x = start.x + 1; x <= end.x - 1; x++) {
					blockPoints.add(new Point(x, start.y));
				}
			}
			//if we reached here start.x will always be greater than end.x
			else {
				for (int x = start.x - 1; x >= end.x + 1; x--) {
					blockPoints.add(new Point(x, start.y));
				}
			}
		}
		for (int player = 0; player < 2; player++) {
			if (bothPlayers.get(player).allPieces.stream().anyMatch(p -> blockPoints.contains(p.position))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return true is there is a piece in-between the start and end point diagonal
	 */
	private static boolean checkPieceInDiagonal(Point start, Point end) {
		List<Point> blockPoints = new ArrayList<>();
		if (start.x < end.x) {
			if (start.y < end.y) {
				for (int i = 1; i < end.x - start.x; i++) {
					blockPoints.add(new Point(start.x + i, start.y + i));
				}
			} else if (start.y > end.y) {
				for (int i = 1; i < end.x - start.x; i++) {
					blockPoints.add(new Point(start.x + i, start.y - i));
				}
			}
		} else if (start.x > end.x) {
			if (start.y < end.y) {
				for (int i = 1; i < start.x - end.x; i++) {
					blockPoints.add(new Point(start.x - i, start.y + i));
				}
			} else if (start.y > end.y) {
				for (int i = 1; i < start.x - end.x; i++) {
					blockPoints.add(new Point(start.x - i, start.y - i));
				}
			}
		}
		for (int player = 0; player < 2; player++) {
			if (bothPlayers.get(player).allPieces.stream().anyMatch(p -> blockPoints.contains(p.position))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return true if the Rook move is legal and false if not
	 */
	private static boolean checkRookMove(Point start, Point end) {
		return !checkPieceInLine(start, end);
	}

	/**
	 * @return true if the Bishop move is legal and false if not
	 */
	private static boolean checkBishopMove(Point start, Point end) {
		return !checkPieceInDiagonal(start, end);
	}


	/**
	 * @return true if the Queen move is legal and false if not
	 */
	private static boolean checkQueenMove(Point start, Point end) {
		return !(checkPieceInDiagonal(start, end) || checkPieceInLine(start, end));
	}


	/**
	 * The most important method, checks the entire logic
	 *
	 * @return true if the move is valid and false if invalid
	 */
	private static boolean checkPlayerMove(Point start, Point end) {

		//Use player+forOpp as the index to get the opposite player from bothPlayers
		int player = isWhiteTurn ? 0 : 1;

		//To be returned
		boolean isMoveCorrect = false;

		//Just removes the en passant pawn for evey alternate move
		//if new en passant is to be added, it's taken care by checkPawnMove, which is called later
		if (enPassantPawn != null && enPassantPawn.isWhite == isWhiteTurn) {
			enPassantPawn = null;
		}

		pieceMoved = getPieceAtPosition(start, isWhiteTurn);

		//pieceMoved should not be null, but still a good check in case of bugs
		if (pieceMoved == null || pieceMoved.isWhite != isWhiteTurn) {
			//System.out.println(" Err : There is no piece found even though you clicked on one");
			return false;
		}

		//Checks if the piece is moving on its own colored piece
		for (Piece p : bothPlayers.get(player).allPieces) {
			if (p.position.equals(end)) {
				return false;
			}
		}

		//Loops through all the possible moves made by the piece
		//Changes isMoveCorrect to true but does not return the value
		for (Point p : pieceMoved.getAllMoves()) {
			if (p.equals(end)) {
				isMoveCorrect = true;
				break;
			}
		}

		if (pieceMoved instanceof Pawn && isMoveCorrect) {
			isMoveCorrect = checkPawnMove(start, end);
		} else if (pieceMoved instanceof Rook && isMoveCorrect) {
			isMoveCorrect = checkRookMove(start, end);
		} else if (pieceMoved instanceof Bishop && isMoveCorrect) {
			isMoveCorrect = checkBishopMove(start, end);
		} else if(pieceMoved instanceof Queen && isMoveCorrect){
			isMoveCorrect = checkQueenMove(start,end);
		}

		return isMoveCorrect;
	}

	/**
	 * Moves the piece and kills any piece to be killed
	 * This is the only method that is allowed to move, add, remove piece!!!
	 */
	private static void movePieceAndKill(Point end) {
		//Make sure to not change any boolean values in this method apart from isPieceKill
		int player = isWhiteTurn ? 0 : 1;
		int forOpp = isWhiteTurn ? 1 : -1;

		if (isEnPassantKill) {
			Point kill = new Point(end.x, pieceMoved.position.y);
			bothPlayers.get(player + forOpp).allPieces.remove(getPieceAtPosition(kill, !isWhiteTurn));
		}

		pieceMoved.position = end;

		for (Piece p : bothPlayers.get(player + forOpp).allPieces) {
			if (p.position.equals(end)) {
				bothPlayers.get(player + forOpp).allPieces.remove(p);
				isPieceKill = true;
				pieceMoved = null;
				return;
			}
		}
		pieceMoved = null;
	}

	/**
	 * @param s Start point of the piece in range (0,0) to (7,7)
	 * @param e End point of the piece in range (0,0) to (7,7)
	 * @return int moveType
	 * 0 - normal move
	 * 1 - kill piece at location
	 * 2 - kill by en passant
	 * 3 - castle king side
	 * 4 - castle queen side
	 * 5 - pawn promotion
	 * @throws InvalidMoveException This is to be caught and undo the move
	 */
	public static int isMoveType(Point s, Point e) throws InvalidMoveException {

		//This is to get the start and end in board coordinates, so it's easy to read and debug
		Point start = correctCoordinates(s);
		Point end = correctCoordinates(e);

		printMoves(start, end);

		if (checkPlayerMove(start, end)) {

			movePieceAndKill(end);

			if (isWhiteTurn) {
				turnNumber++;
			}
			isWhiteTurn = !isWhiteTurn;

			//all return statements should be below this
			pieceMoved = null;
			//return statements from here please...
			if (isPieceKill && !isEnPassantKill) {
				isPieceKill = false;
				return 1;
			}
			if (isEnPassantKill) {
				isEnPassantKill = false;
				return 2;
			}
			return 0;
		} else {
			//Probably redundant checks but maintains the board status for the next move
			isPieceKill = false;
			isEnPassantKill = false;
			pieceMoved = null;

			throw new InvalidMoveException();
		}
	}
}