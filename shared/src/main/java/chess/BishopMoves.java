package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoves implements ChessMoveCalculator {

    ChessPiece piece;
    public BishopMoves(ChessPiece newPiece) {
        piece = newPiece;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        Collection<ChessMove> possibleMoves = new ArrayList<>();
        boolean validSpace = true;
        int curRow = myPosition.getRow();
        int curCol = myPosition.getColumn();
        // Check the left and behind spaces
        while (validSpace) {
            --curCol;
            --curRow;
            //Check for out of bounds
            if ((curRow < 1 || curRow > 8) || (curCol < 1 || curCol > 8)) {
                validSpace = false;
                continue;
            }

            ChessPosition curSpace = new ChessPosition(curRow, curCol);
            if (board.isSpaceEmpty(curSpace)) {
                possibleMoves.add(new ChessMove(myPosition, curSpace, null));
            }
            else if (board.getPiece(curSpace).getTeamColor() != piece.getTeamColor()){
                possibleMoves.add(new ChessMove(myPosition, curSpace,null));
                validSpace = false;
            }
            else {
                validSpace = false;
            }
        }

        validSpace = true;
        curRow = myPosition.getRow();
        curCol = myPosition.getColumn();
        // Check the left and forward spaces
        while (validSpace) {
            --curCol;
            ++curRow;
            //Check for out of bounds
            if ((curRow < 1 || curRow > 8) || (curCol < 1 || curCol > 8)) {
                validSpace = false;
                continue;
            }

            ChessPosition curSpace = new ChessPosition(curRow, curCol);
            if (board.isSpaceEmpty(curSpace)) {
                possibleMoves.add(new ChessMove(myPosition, curSpace, null));
            }
            else if (board.getPiece(curSpace).getTeamColor() != piece.getTeamColor()){
                possibleMoves.add(new ChessMove(myPosition, curSpace, null));
                validSpace = false;
            }
            else {
                validSpace = false;
            }
        }

        validSpace = true;
        curRow = myPosition.getRow();
        curCol = myPosition.getColumn();
        // Check the right and behind spaces
        while (validSpace) {
            ++curCol;
            --curRow;
            //Check for out of bounds
            if ((curRow < 1 || curRow > 8) || (curCol < 1 || curCol > 8)) {
                validSpace = false;
                continue;
            }

            ChessPosition curSpace = new ChessPosition(curRow, curCol);
            if (board.isSpaceEmpty(curSpace)) {
                possibleMoves.add(new ChessMove(myPosition, curSpace, null));
            }
            else if (board.getPiece(curSpace).getTeamColor() != piece.getTeamColor()){
                possibleMoves.add(new ChessMove(myPosition, curSpace, null));
                validSpace = false;
            }
            else {
                validSpace = false;
            }
        }

        validSpace = true;
        curRow = myPosition.getRow();
        curCol = myPosition.getColumn();
        // Check the right and forward spaces
        while (validSpace) {
            ++curCol;
            ++curRow;
            //Check for out of bounds
            if ((curRow < 1 || curRow > 8) || (curCol < 1 || curCol > 8)) {
                validSpace = false;
                continue;
            }

            ChessPosition curSpace = new ChessPosition(curRow, curCol);
            if (board.isSpaceEmpty(curSpace)) {
                possibleMoves.add(new ChessMove(myPosition, curSpace, null));
            }
            else if (board.getPiece(curSpace).getTeamColor() != piece.getTeamColor()){
                possibleMoves.add(new ChessMove(myPosition, curSpace, null));
                validSpace = false;
            }
            else {
                validSpace = false;
            }

        }
        return possibleMoves;
    }
}
