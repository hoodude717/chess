package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoves implements ChessMoveCalculator {

    ChessPiece piece;
    public PawnMoves(ChessPiece newPiece) {
        piece = newPiece;
    }

    public boolean isOutofBounds(ChessPosition curPosition) {
        boolean outofbounds = curPosition.getRow() > 8 || curPosition.getRow() < 1; //Check rows
        if (curPosition.getColumn() > 8 || curPosition.getColumn() < 1) { outofbounds = true;} //Check Cols
        return outofbounds;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        int curRow = myPosition.getRow();
        int curCol = myPosition.getColumn();

        ChessPiece.PieceType pawn = ChessPiece.PieceType.PAWN;

        // Check the position based on color
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            ChessPosition tempPosition = new ChessPosition(curRow+1, curCol);
            if (!isOutofBounds(tempPosition) && board.isSpaceEmpty(tempPosition)) { // Check for empty spaces in front
                possibleMoves.add(new ChessMove(myPosition, tempPosition, pawn));
                if (curRow == 2) { //In Starting Position
                    tempPosition = new ChessPosition(curRow+2, curCol);
                    if (!isOutofBounds(tempPosition) && board.isSpaceEmpty(tempPosition)) {
                        possibleMoves.add(new ChessMove(myPosition,tempPosition, pawn));
                    }
                }
            }
            // Check attacking Positions
            ChessPiece tempPiece = board.getPiece(new ChessPosition(curRow+1, curCol-1)); //Front Left
            if (tempPiece != null && tempPiece.getTeamColor() != pieceColor ) {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(curRow+1, curCol-1), pawn));
            }

            tempPiece = board.getPiece(new ChessPosition(curRow+1, curCol+1)); //Front Right
            if (tempPiece != null && tempPiece.getTeamColor() != pieceColor ) {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(curRow+1, curCol+1), pawn));
            }
        }
        else { // Is Black Piece
            if (board.isSpaceEmpty(new ChessPosition(curRow-1, curCol))) { // Check for empty spaces in front
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(curRow-1, curCol), pawn));
                if (curRow == 7) { //In Starting Position
                    if (board.isSpaceEmpty(new ChessPosition(curRow-2, curCol))) {
                        possibleMoves.add(new ChessMove(myPosition, new ChessPosition(curRow-2, curCol), piece.getPieceType()));
                    }
                }
            }
            // Check attacking Positions
            ChessPiece tempPiece = board.getPiece(new ChessPosition(curRow-1, curCol-1)); //Front Left
            if (tempPiece != null && tempPiece.getTeamColor() != pieceColor ) {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(curRow-1, curCol-1), pawn));
            }

            tempPiece = board.getPiece(new ChessPosition(curRow-1, curCol+1)); //Front Right
            if (tempPiece != null && tempPiece.getTeamColor() != pieceColor ) {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(curRow-1, curCol+1), pawn));
            }
        }

        return possibleMoves;
    }
}
