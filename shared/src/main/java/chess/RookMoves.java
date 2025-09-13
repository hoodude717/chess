package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoves implements ChessMoveCalculator{
    ChessPiece piece;
    public RookMoves(ChessPiece newPiece) {
        piece = newPiece;
    }


//    private boolean getValidMovesWhile(Collection<ChessMove> possibleMoves, ChessBoard board, ChessPosition myPosition, int curRow, int curCol) {
//        boolean validSpace = true;
//
//        if ((curRow < 1 || curRow > 8) || (curCol < 1 || curCol > 8)) {
//            return false;
//        }
//        ChessPosition curSpace = new ChessPosition(curRow, curCol);
//        if (board.isSpaceEmpty(curSpace)) {
//            possibleMoves.add(new ChessMove(myPosition, curSpace, null));
//        } else if (board.getPiece(curSpace).getTeamColor() != piece.getTeamColor()){
//            possibleMoves.add(new ChessMove(myPosition, curSpace, null));
//            validSpace = false;
//        } else { validSpace = false; }
//
//        return validSpace;
//    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        boolean validSpace = true;
        int curRow = myPosition.getRow();
        int curCol = myPosition.getColumn();
        // Check the front spaces
        while (validSpace) {
            ++curRow;
            validSpace = getValidMovesWhile(possibleMoves, board, myPosition, curRow, curCol);
        }

        validSpace = true;
        curRow = myPosition.getRow();
        curCol = myPosition.getColumn();
        // Check the left spaces
        while (validSpace) {
            --curCol;
            validSpace = getValidMovesWhile(possibleMoves, board, myPosition, curRow, curCol);

        }

        validSpace = true;
        curRow = myPosition.getRow();
        curCol = myPosition.getColumn();
        // Check the behind spaces
        while (validSpace) {
            --curRow;
            validSpace = getValidMovesWhile(possibleMoves, board, myPosition, curRow, curCol);
        }

        validSpace = true;
        curRow = myPosition.getRow();
        curCol = myPosition.getColumn();
        // Check the right spaces
        while (validSpace) {
            ++curCol;
            validSpace = getValidMovesWhile(possibleMoves, board, myPosition, curRow, curCol);
        }




        return possibleMoves;
    }


}
