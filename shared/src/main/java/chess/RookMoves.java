package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoves implements ChessMoveCalculator{
    ChessPiece piece;
    private Collection<ChessMove> attacks = new ArrayList<>();

    public RookMoves(ChessPiece newPiece) {
        piece = newPiece;
    }


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
        // Check the behind spaces
        while (validSpace) {
            --curRow;
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
        // Check the right spaces
        while (validSpace) {
            ++curCol;
            validSpace = getValidMovesWhile(possibleMoves, board, myPosition, curRow, curCol);
        }

        return possibleMoves;
    }

    @Override
    public Collection<ChessMove> attackMoves(ChessBoard board, ChessPosition myPosition) {
        return attacks;
    }

}
