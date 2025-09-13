package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoves implements ChessMoveCalculator {


    ChessPiece.PieceType rook = ChessPiece.PieceType.ROOK;
    ChessPiece.PieceType knight = ChessPiece.PieceType.KNIGHT;
    ChessPiece.PieceType bishop = ChessPiece.PieceType.BISHOP;
    ChessPiece.PieceType queen = ChessPiece.PieceType.QUEEN;

    ChessPiece piece;
    ChessGame.TeamColor pieceColor;
    public PawnMoves(ChessPiece newPiece) {
        piece = newPiece;
        pieceColor = newPiece.getTeamColor();

    }

    public boolean isInBounds(ChessPosition curPosition) {
        boolean outofbounds = curPosition.getRow() > 8 || curPosition.getRow() < 1; //Check rows
        if (curPosition.getColumn() > 8 || curPosition.getColumn() < 1) { outofbounds = true;} //Check Cols
        return !outofbounds;
    }

    private Collection<ChessMove> checkPromotionWhite(ChessPosition myPosition, ChessPosition tempPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        if (tempPosition.getRow() == 8) {
            possibleMoves.add(new ChessMove(myPosition, tempPosition, rook));
            possibleMoves.add(new ChessMove(myPosition, tempPosition, knight));
            possibleMoves.add(new ChessMove(myPosition, tempPosition, bishop));
            possibleMoves.add(new ChessMove(myPosition, tempPosition, queen));
        }
        else {
            possibleMoves.add(new ChessMove(myPosition, tempPosition, null));
        }
        return possibleMoves;
    }

    private Collection<ChessMove> checkPromotionBlack(ChessPosition myPosition, ChessPosition tempPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        if (tempPosition.getRow() == 1) {
            possibleMoves.add(new ChessMove(myPosition, tempPosition, rook));
            possibleMoves.add(new ChessMove(myPosition, tempPosition, knight));
            possibleMoves.add(new ChessMove(myPosition, tempPosition, bishop));
            possibleMoves.add(new ChessMove(myPosition, tempPosition, queen));
        }
        else {
            possibleMoves.add(new ChessMove(myPosition, tempPosition, null));
        }
        return possibleMoves;
    }

    private Collection<ChessMove> checkAttackSpacesWhite(ChessBoard board, ChessPosition myPosition, ChessPosition tempPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        if (isInBounds(tempPosition)) {
            ChessPiece tempPiece = board.getPiece(tempPosition);
            if (tempPiece != null && tempPiece.getTeamColor() != pieceColor) {
                possibleMoves.addAll(checkPromotionWhite(myPosition, tempPosition));
            }
        }
        return possibleMoves;
    }

    private Collection<ChessMove> checkAttackSpacesBlack(ChessBoard board, ChessPosition myPosition, ChessPosition tempPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        if (isInBounds(tempPosition)) {
            ChessPiece tempPiece = board.getPiece(tempPosition);
            if (tempPiece != null && tempPiece.getTeamColor() != pieceColor) {
                possibleMoves.addAll(checkPromotionBlack(myPosition, tempPosition));
            }
        }
        return possibleMoves;
    }


    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int curRow = myPosition.getRow();
        int curCol = myPosition.getColumn();
        // Check the position based on color
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            ChessPosition tempPosition = new ChessPosition(curRow + 1, curCol);
            if (isInBounds(tempPosition) && board.isSpaceEmpty(tempPosition)) { // Check for empty spaces in front
                possibleMoves.addAll(checkPromotionWhite(myPosition, tempPosition));
                if (curRow == 2) { //In Starting Position
                    tempPosition = new ChessPosition(curRow + 2, curCol);
                    if (isInBounds(tempPosition) && board.isSpaceEmpty(tempPosition)) {
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, null));
                    }
                }
            }
            // Check attacking Positions
            tempPosition = new ChessPosition(curRow + 1, curCol - 1);
            //New Function Check Attacking Position
            possibleMoves.addAll(checkAttackSpacesWhite(board, myPosition, tempPosition));
            tempPosition = new ChessPosition(curRow+1, curCol+1);
            possibleMoves.addAll(checkAttackSpacesWhite(board, myPosition, tempPosition));

        }
        else { // Is Black Piece
            ChessPosition tempPosition = new ChessPosition(curRow-1, curCol);
            if (isInBounds(tempPosition) && board.isSpaceEmpty(tempPosition)) { // Check for empty spaces in front
                possibleMoves.addAll(checkPromotionBlack(myPosition, tempPosition));
                if (curRow == 7) { //In Starting Position
                    tempPosition = new ChessPosition(curRow - 2, curCol);
                    if (isInBounds(tempPosition) && board.isSpaceEmpty(tempPosition)) {
                        possibleMoves.add(new ChessMove(myPosition, tempPosition, null));
                    }
                }
            }
            // Check attacking Positions
            tempPosition = new ChessPosition(curRow - 1, curCol - 1);
            possibleMoves.addAll(checkAttackSpacesBlack(board, myPosition, tempPosition));

            tempPosition = new ChessPosition(curRow-1, curCol+1);
            possibleMoves.addAll(checkAttackSpacesBlack(board, myPosition, tempPosition));
        }

        return possibleMoves;
    }
}
