package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoves implements ChessMoveCalculator {

    ChessPiece piece;
    public PawnMoves(ChessPiece newPiece) {
        piece = newPiece;
    }

    public boolean isInBounds(ChessPosition curPosition) {
        boolean outofbounds = curPosition.getRow() > 8 || curPosition.getRow() < 1; //Check rows
        if (curPosition.getColumn() > 8 || curPosition.getColumn() < 1) { outofbounds = true;} //Check Cols
        return !outofbounds;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        int curRow = myPosition.getRow();
        int curCol = myPosition.getColumn();

        ChessPiece.PieceType rook = ChessPiece.PieceType.ROOK;
        ChessPiece.PieceType knight = ChessPiece.PieceType.KNIGHT;
        ChessPiece.PieceType bishop = ChessPiece.PieceType.BISHOP;
        ChessPiece.PieceType queen = ChessPiece.PieceType.QUEEN;


        // Check the position based on color
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            ChessPosition tempPosition = new ChessPosition(curRow + 1, curCol);
            if (isInBounds(tempPosition) && board.isSpaceEmpty(tempPosition)) { // Check for empty spaces in front
                 if (curRow+1 == 8) {
                    possibleMoves.add(new ChessMove(myPosition, tempPosition, rook));
                    possibleMoves.add(new ChessMove(myPosition, tempPosition, knight));
                    possibleMoves.add(new ChessMove(myPosition, tempPosition, bishop));
                    possibleMoves.add(new ChessMove(myPosition, tempPosition, queen));
                }
                 else {
                     possibleMoves.add(new ChessMove(myPosition, tempPosition, null));
                 }
                 if (curRow == 2) { //In Starting Position
                    tempPosition = new ChessPosition(curRow + 2, curCol);
                    if (isInBounds(tempPosition) && board.isSpaceEmpty(tempPosition)) {
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, null));
                    }
                }

            }
            // Check attacking Positions
            tempPosition = new ChessPosition(curRow + 1, curCol - 1);
            if (isInBounds(tempPosition)) {
                ChessPiece tempPiece = board.getPiece(tempPosition); //Front Left
                if (tempPiece != null && tempPiece.getTeamColor() != pieceColor) {
                    if (curRow+1 == 8) {
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, rook));
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, knight));
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, bishop));
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, queen));
                    }
                    else {
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, null));
                    }
                }
            }
            tempPosition = new ChessPosition(curRow+1, curCol+1);
            if (isInBounds(tempPosition)) {
                ChessPiece tempPiece = board.getPiece(tempPosition); //Front Right
                if (tempPiece != null && tempPiece.getTeamColor() != pieceColor) {
                    if (curRow+1 == 8) {
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, rook));
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, knight));
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, bishop));
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, queen));
                    }
                    else {
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, null));
                    }
                }
            }
        }
        else { // Is Black Piece
            ChessPosition tempPosition = new ChessPosition(curRow-1, curCol);
            if (isInBounds(tempPosition) && board.isSpaceEmpty(tempPosition)) { // Check for empty spaces in front
                if (curRow-1 ==1) {
                    possibleMoves.add(new ChessMove(myPosition, tempPosition, rook));
                    possibleMoves.add(new ChessMove(myPosition, tempPosition, knight));
                    possibleMoves.add(new ChessMove(myPosition, tempPosition, bishop));
                    possibleMoves.add(new ChessMove(myPosition, tempPosition, queen));
                }
                else {
                    possibleMoves.add(new ChessMove(myPosition, tempPosition, null));
                }
                if (curRow == 7) { //In Starting Position
                    tempPosition = new ChessPosition(curRow - 2, curCol);
                    if (isInBounds(tempPosition) && board.isSpaceEmpty(tempPosition)) {
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, null));
                    }
                }
            }
            // Check attacking Positions
            tempPosition = new ChessPosition(curRow - 1, curCol - 1);
            if (isInBounds(tempPosition)) {
                ChessPiece tempPiece = board.getPiece(tempPosition); //Front Left
                if (tempPiece != null && tempPiece.getTeamColor() != pieceColor) {
                    if (curRow-1 == 1) {
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, rook));
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, knight));
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, bishop));
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, queen));
                    }
                    else {
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, null));
                    }
                }
            }
            tempPosition = new ChessPosition(curRow-1, curCol+1);
            if (isInBounds(tempPosition)) {
                ChessPiece tempPiece = board.getPiece(tempPosition); //Front Right
                if (tempPiece != null && tempPiece.getTeamColor() != pieceColor) {
                    if (curRow-1 == 1) {
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, rook));
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, knight));
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, bishop));
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, queen));
                    }
                    else {
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, null));
                    }
                }
            }
        }

        return possibleMoves;
    }
}
