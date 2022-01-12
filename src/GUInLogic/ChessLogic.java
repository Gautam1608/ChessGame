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

    private final static Player whitePlayer = new Player(true);
    private final static Player blackPlayer = new Player(false);
    private final static List<Player> bothPlayers = new ArrayList<>(Arrays.asList(whitePlayer, blackPlayer));
    private final static King whiteKing = (King) getPieceAtPosition(new Point(5, 1), true);
    private final static King blackKing = (King) getPieceAtPosition(new Point(5, 8), false);
    private final static List<King> bothKings = new ArrayList<>(Arrays.asList(whiteKing, blackKing));

    //Make this null before every return statement
    private static Piece pieceMoved = null;
    //Make this false before the kill return statement
    private static boolean isPieceKill = false;

    private static Pawn enPassantPawn = null;
    //Make this false before the en passant return statement
    private static boolean isEnPassantKill = false;
    private static Point promotionPoint = null;

    private static boolean isShortCastleBlack = true;
    private static boolean isLongCastleBlack = true;
    private static boolean isShortCastleWhite = true;
    private static boolean isLongCastleWhite = true;
    private static boolean hasCastles = false;

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
        }
        else {
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
     * @return true is there is a piece in-between the start and end point line
     */

    private static boolean checkPieceInLine(Point start, Point end) {
        List<Point> blockPoints = new ArrayList<>();
        if (start.x == end.x) {
            if (start.y < end.y) {
                for (int y = start.y + 1; y <= end.y - 1; y++) {
                    blockPoints.add(new Point(start.x, y));
                }
            }
            else if (start.y > end.y) {
                for (int y = start.y - 1; y >= end.y + 1; y--) {
                    blockPoints.add(new Point(start.x, y));
                }
            }
        }
        else if (start.y == end.y) {
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
            }
            else if (start.y > end.y) {
                for (int i = 1; i < end.x - start.x; i++) {
                    blockPoints.add(new Point(start.x + i, start.y - i));
                }
            }
        }
        else if (start.x > end.x) {
            if (start.y < end.y) {
                for (int i = 1; i < start.x - end.x; i++) {
                    blockPoints.add(new Point(start.x - i, start.y + i));
                }
            }
            else if (start.y > end.y) {
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
     * @param isWhite if true checks all the black pieces and vise-versa
     * @return true when the point is in check by opposite color pieces
     */
    private static boolean checkCheckInPoint(Point point, boolean isWhite) {
        for (Piece p : bothPlayers.get(isWhite ? 1 : 0).allPieces) {
            if (p.getAllMoves().contains(point)) {
                if (p instanceof Pawn) {
                    if (checkPawnMove(p.position, point))
                        return true;
                }
                else if (p instanceof Queen) {
                    if (checkQueenMove(p.position, point)) {
                        return true;
                    }
                }
                else if (p instanceof Rook) {
                    if (checkRookMove(p.position, point)) {
                        return true;
                    }
                }
                else if (p instanceof King) {
                    if (p.getAllMoves().contains(point)) {
                        if (Math.abs(p.position.x - point.x) != 2)
                            return true;
                    }
                }
                else if (p instanceof Bishop) {
                    if (checkBishopMove(p.position, point)) {
                        return true;
                    }
                }
                else if (p instanceof Knight) {
                    if (p.getAllMoves().contains(point)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean checkCheckMate(boolean isWhite) {
        int oppPlayer = isWhite ? 1 : 0;
        int player = isWhite ? 0 : 1;
        for (Piece piece : bothPlayers.get(player).allPieces) {
            Point start = piece.position;
            for (Point point : piece.getAllMoves()) {
                if (piece instanceof Bishop || piece instanceof Queen) {
                    if (point.x - piece.position.x == 0 || point.y - piece.position.y == 0) {
                        continue;
                    }
                    if (!checkPieceInDiagonal(piece.position, point)) {
                        Piece sameColorPiece = getPieceAtPosition(point, isWhite);
                        Piece killed = getPieceAtPosition(point, !isWhite);
                        bothPlayers.get(oppPlayer).allPieces.remove(killed);
                        piece.position = point;
                        if (!checkCheckInPoint(bothKings.get(player).position, isWhite) && sameColorPiece == null) {
                            if (killed != null) {
                                bothPlayers.get(oppPlayer).allPieces.add(killed);
                            }
                            piece.position = start;
                            return false;
                        }
                        if (killed != null) {
                            bothPlayers.get(oppPlayer).allPieces.add(killed);
                        }
                        piece.position = start;
                    }
                }
                if (piece instanceof Rook || piece instanceof Queen) {
                    if (!(point.x - piece.position.x == 0 || point.y - piece.position.y == 0)) {
                        continue;
                    }
                    if (!checkPieceInLine(piece.position, point)) {
                        Piece sameColorPiece = getPieceAtPosition(point, isWhite);
                        Piece killed = getPieceAtPosition(point, !isWhite);
                        bothPlayers.get(oppPlayer).allPieces.remove(killed);
                        piece.position = point;
                        if (!checkCheckInPoint(bothKings.get(player).position, isWhite) && sameColorPiece == null) {
                            if (killed != null) {
                                bothPlayers.get(oppPlayer).allPieces.add(killed);
                            }
                            piece.position = start;
                            return false;
                        }
                        if (killed != null) {
                            bothPlayers.get(oppPlayer).allPieces.add(killed);
                        }
                        piece.position = start;
                    }
                }
                if (piece instanceof Knight || piece instanceof King) {
                    if (piece instanceof King && Math.abs(piece.position.x - point.x) == 2) {
                        break;
                    }
                    Piece sameColorPiece = getPieceAtPosition(point, isWhite);
                    Piece killed = getPieceAtPosition(point, !isWhite);
                    bothPlayers.get(oppPlayer).allPieces.remove(killed);
                    piece.position = point;
                    if (!checkCheckInPoint(bothKings.get(player).position, isWhite) && sameColorPiece == null) {
                        if (killed != null) {
                            bothPlayers.get(oppPlayer).allPieces.add(killed);
                        }
                        piece.position = start;
                        return false;
                    }
                    if (killed != null) {
                        bothPlayers.get(oppPlayer).allPieces.add(killed);
                    }
                    piece.position = start;
                }
                if (piece instanceof Pawn) {
                    if (Math.abs(piece.position.y - point.y) == 2) {
                        int checkY = isWhite ? 1 : -1;
                        if (getPieceAtPosition(new Point(start.x, start.y + checkY), isWhite) == null && getPieceAtPosition(new Point(start.x, start.y + checkY), !isWhite) == null
                                && getPieceAtPosition(point, isWhite) == null && getPieceAtPosition(point, !isWhite) == null) {
                            piece.position = point;
                            if (!checkCheckInPoint(bothKings.get(player).position, isWhite)) {
                                piece.position = start;
                                return false;
                            }
                            piece.position = start;
                        }
                    }
                    if (piece.position.x - point.x != 0) {
                        if (enPassantPawn != null) {
                            if (enPassantPawn.position.equals(new Point(point.x, piece.position.y))) {
                                bothPlayers.get(oppPlayer).allPieces.remove(getPieceAtPosition(enPassantPawn.position, enPassantPawn.isWhite));
                                piece.position = point;
                                if (!checkCheckInPoint(bothKings.get(player).position, isWhite)) {
                                    bothPlayers.get(oppPlayer).allPieces.add(enPassantPawn);
                                    piece.position = start;
                                    return false;
                                }
                                bothPlayers.get(oppPlayer).allPieces.add(enPassantPawn);
                                piece.position = start;
                            }
                        }
                        Piece killed = getPieceAtPosition(point, !isWhite);
                        if (killed != null) {
                            bothPlayers.get(oppPlayer).allPieces.remove(killed);
                            piece.position = point;
                            if (!checkCheckInPoint(bothKings.get(player).position, isWhite)) {
                                bothPlayers.get(oppPlayer).allPieces.add(killed);
                                piece.position = start;
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }


    private static void checkCastlePossible(Point start, Point end) {
        if (checkCheckInPoint(start, isWhiteTurn)) {
            hasCastles = false;
            return;
        }
        if (start.equals(new Point(5, 1))) {
            if ((start.x - end.x) < 0 && isShortCastleWhite) {
                if (checkCheckInPoint(new Point(6, start.y), true) || checkCheckInPoint(new Point(7, start.y), true)) {
                    hasCastles = false;
                    return;
                }
                for (int i = 0; i < 2; i++) {
                    Piece p1 = getPieceAtPosition(new Point(6, 1), i == 0);
                    Piece p2 = getPieceAtPosition(new Point(7, 1), i == 0);
                    if ((p1 != null) || (p2 != null)) {
                        hasCastles = false;
                        return;
                    }
                }

                hasCastles = true;
                return;
            }
            else if (start.x - end.x > 0 && isLongCastleWhite) {
                if (checkCheckInPoint(new Point(4, start.y), true) || checkCheckInPoint(new Point(3, start.y), true)) {
                    hasCastles = false;
                    return;
                }
                for (int i = 0; i < 2; i++) {
                    Piece p1 = getPieceAtPosition(new Point(4, 1), i == 0);
                    Piece p2 = getPieceAtPosition(new Point(3, 1), i == 0);
                    Piece p3 = getPieceAtPosition(new Point(2, 1), i == 0);
                    if ((p1 != null) || (p2 != null) || (p3 != null)) {
                        hasCastles = false;
                        return;
                    }
                }
                hasCastles = true;
                return;
            }
        }
        else if (start.equals(new Point(5, 8))) {
            if (checkCheckInPoint(new Point(6, start.y), false) || checkCheckInPoint(new Point(7, start.y), false)) {
                hasCastles = false;
                return;
            }
            if ((start.x - end.x) < 0 && isShortCastleBlack) {
                for (int i = 0; i < 2; i++) {
                    Piece p1 = getPieceAtPosition(new Point(6, 8), i == 0);
                    Piece p2 = getPieceAtPosition(new Point(7, 8), i == 0);
                    if ((p1 != null) || (p2 != null)) {
                        hasCastles = false;
                        return;
                    }
                }
                hasCastles = true;
                return;
            }
            else if (start.x - end.x > 0 && isLongCastleBlack) {
                if (checkCheckInPoint(new Point(4, start.y), false) || checkCheckInPoint(new Point(3, start.y), false)) {
                    hasCastles = false;
                    return;
                }
                for (int i = 0; i < 2; i++) {
                    Piece p1 = getPieceAtPosition(new Point(4, 8), i == 0);
                    Piece p2 = getPieceAtPosition(new Point(3, 8), i == 0);
                    Piece p3 = getPieceAtPosition(new Point(2, 8), i == 0);
                    if ((p1 != null) || (p2 != null) || (p3 != null)) {
                        hasCastles = false;
                        return;
                    }
                }
                hasCastles = true;
                return;
            }
        }
        hasCastles = false;
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
        for (int i = 0; i < 2; i++) {
            for (Piece oppPiece : bothPlayers.get(i).allPieces) {
                if (oppPiece.position.equals(end)) {
                    return false;
                }
            }
        }
        return true;
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
        }
        else if (pieceMoved instanceof Rook && isMoveCorrect) {
            isMoveCorrect = checkRookMove(start, end);
            if (isMoveCorrect && pieceMoved.isWhite) {
                if (pieceMoved.position.equals(new Point(1, 1)) && isLongCastleWhite) {
                    isLongCastleWhite = false;
                }
                else if (pieceMoved.position.equals(new Point(8, 1)) && isShortCastleWhite) {
                    isShortCastleWhite = false;
                }
            }
            //piece moved is always black here
            else if (isMoveCorrect) {
                if (pieceMoved.position.equals(new Point(1, 8)) && isLongCastleBlack) {
                    isLongCastleBlack = false;
                }
                else if (pieceMoved.position.equals(new Point(8, 8)) && isShortCastleBlack) {
                    isShortCastleBlack = false;
                }
            }
        }
        else if (pieceMoved instanceof Bishop && isMoveCorrect) {
            isMoveCorrect = checkBishopMove(start, end);
        }
        else if (pieceMoved instanceof Queen && isMoveCorrect) {
            isMoveCorrect = checkQueenMove(start, end);
        }
        else if (pieceMoved instanceof King && isMoveCorrect) {
            if (isWhiteTurn && Math.abs(start.x - end.x) != 2 && (isLongCastleWhite || isShortCastleWhite)) {
                isShortCastleWhite = false;
                isLongCastleWhite = false;
            }
            else if (!isWhiteTurn && Math.abs(start.x - end.x) != 2 && (isShortCastleBlack || isLongCastleBlack)) {
                isShortCastleBlack = false;
                isLongCastleBlack = false;
            }
            else if (Math.abs(start.x - end.x) == 2) {
                checkCastlePossible(start, end);
                if (!hasCastles) {
                    return false;
                }
            }
        }
        return isMoveCorrect;
    }

    /**
     * Moves the piece and kills any piece to be killed
     * This is the only method that is allowed to move, add, remove piece!!!
     */
    private static void movePieceAndKill(Point end) throws InvalidMoveException {
        //Make sure to not change any boolean values in this method apart from isPieceKill
        int player = isWhiteTurn ? 0 : 1;
        int forOpp = isWhiteTurn ? 1 : -1;
        Point start = new Point(pieceMoved.position);

        if (isEnPassantKill) {
            Point kill = new Point(end.x, pieceMoved.position.y);
            bothPlayers.get(player + forOpp).allPieces.remove(getPieceAtPosition(kill, !isWhiteTurn));
        }

        pieceMoved.position = end;
        if (hasCastles) {
            Piece p;
            if (pieceMoved.position.x == 7) {
                p = getPieceAtPosition(new Point(8, end.y), isWhiteTurn);
                assert p != null;
                p.position = new Point(6, end.y);

            }
            else {
                p = getPieceAtPosition(new Point(1, end.y), isWhiteTurn);
                assert p != null;
                p.position = new Point(4, end.y);
            }
            pieceMoved = null;
            return;
        }
        Piece killedPiece = null;
        for (Piece p : bothPlayers.get(player + forOpp).allPieces) {
            if (p.position.equals(end)) {
                bothPlayers.get(player + forOpp).allPieces.remove(p);
                killedPiece = p;
                isPieceKill = true;
                break;
            }
        }
        if (checkCheckInPoint(bothKings.get(player).position, isWhiteTurn)) {
            if (killedPiece != null) {
                bothPlayers.get(player + forOpp).allPieces.add(killedPiece);
            }
            pieceMoved.position = start;
            isPieceKill = false;
            isEnPassantKill = false;
            pieceMoved = null;
            throw new InvalidMoveException();
        }
    }

    public static boolean updatePromotion(char newPiece) {
        Piece p = null;
        if (newPiece == 'Q') {
            p = new Queen(promotionPoint.x, promotionPoint.y, !isWhiteTurn);
        }
        else if (newPiece == 'B') {
            p = new Bishop(promotionPoint.x, promotionPoint.y, !isWhiteTurn);
        }
        else if (newPiece == 'R') {
            p = new Rook(promotionPoint.x, promotionPoint.y, !isWhiteTurn);
        }
        else if (newPiece == 'N') {
            p = new Knight(promotionPoint.x, promotionPoint.y, !isWhiteTurn);
        }
        bothPlayers.get(!isWhiteTurn ? 0 : 1).allPieces.add(p);
        promotionPoint = null;
        return checkCheckMate(isWhiteTurn);
    }

    /**
     * @param s Start point of the piece in range (0,0) to (7,7)
     * @param e End point of the piece in range (0,0) to (7,7)
     * @return int moveType
     * 0 - kill piece at location
     * 1 - kill by en passant
     * 2 - castling
     * 3 - pawn promotion
     * 4 - checkmate
     * @throws InvalidMoveException This is to be caught and undo the move
     */
    public static boolean[] isMoveType(Point s, Point e) throws InvalidMoveException {
        boolean[] returnValue = new boolean[]{false, false, false, false, false, false};
        boolean promotion = false;

        //This is to get the start and end in board coordinates, so it's easy to read and debug
        Point start = correctCoordinates(s);
        Point end = correctCoordinates(e);



        if (checkPlayerMove(start, end)) {

            printMoves(start, end);
            movePieceAndKill(end);

            if (isWhiteTurn) {
                turnNumber++;
            }
            if (isPieceKill && !isEnPassantKill) {
                isPieceKill = false;
                returnValue[0] = true;
            }
            if (isEnPassantKill) {
                isEnPassantKill = false;
                returnValue[1] = true;
            }
            if (hasCastles) {
                hasCastles = false;
                if (isWhiteTurn) {
                    isLongCastleWhite = false;
                    isShortCastleWhite = false;
                }
                else {
                    isLongCastleBlack = false;
                    isShortCastleBlack = false;
                }
                returnValue[2] = true;
            }
            if (pieceMoved instanceof Pawn && ((isWhiteTurn && pieceMoved.position.y == 8) || (!isWhiteTurn && pieceMoved.position.y == 1))) {
                bothPlayers.get(isWhiteTurn ? 0 : 1).allPieces.remove(pieceMoved);
                promotion = true;
                promotionPoint = pieceMoved.position;
                returnValue[3] = true;
            }
            if (checkCheckMate(!isWhiteTurn)) {
                if (checkCheckInPoint(bothKings.get(!isWhiteTurn ? 0 : 1).position, !isWhiteTurn) && !promotion) {
                    returnValue[4] = true;
                }
                else {
                    returnValue[5] = true;
                }
                bothPlayers.remove(0);
                bothPlayers.remove(0);
                isWhiteTurn = true;
                turnNumber = 1;
                whitePlayer.reset();
                blackPlayer.reset();
                bothPlayers.addAll(Arrays.asList(whitePlayer, blackPlayer));
                assert whiteKing != null;
                whiteKing.position = new Point(5, 1);
                assert blackKing != null;
                blackKing.position = new Point(5, 8);
                bothKings.clear();
                bothKings.addAll(Arrays.asList(whiteKing, blackKing));
                pieceMoved = null;
                isPieceKill = false;
                enPassantPawn = null;
                isEnPassantKill = false;
                isShortCastleBlack = true;
                isLongCastleBlack = true;
                isShortCastleWhite = true;
                isLongCastleWhite = true;
                hasCastles = false;

                promotionPoint = null;
                return returnValue;
            }
            isWhiteTurn = !isWhiteTurn;
            //all return statements should be below this
            pieceMoved = null;
            //return statements from here please...
            return returnValue;
        }
        else {
            //Probably redundant checks but maintains the board status for the next move
            isPieceKill = false;
            isEnPassantKill = false;
            pieceMoved = null;
            promotionPoint = null;
            hasCastles = false;
            throw new InvalidMoveException();
        }
    }
}
