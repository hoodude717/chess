package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoves implements ChessMoveCalculator{
    ChessPiece piece;
    public QueenMoves(ChessPiece newPiece) {
        piece = newPiece;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var possibleMoves = new ArrayList<ChessMove>();

        boolean validSpace = true;
        int curRow = myPosition.getRow();
        int curCol = myPosition.getColumn();
        // Check the left and behind spaces
        while (validSpace) {
            --curCol;
            --curRow;
            validSpace = getValidMovesWhile(possibleMoves, board, myPosition, curRow, curCol);
        }

        validSpace = true;
        curRow = myPosition.getRow();
        curCol = myPosition.getColumn();
        // Check the left and forward spaces
        while (validSpace) {
            --curCol;
            ++curRow;
            validSpace = getValidMovesWhile(possibleMoves, board, myPosition, curRow, curCol);

        }

        validSpace = true;
        curRow = myPosition.getRow();
        curCol = myPosition.getColumn();
        // Check the right and behind spaces
        while (validSpace) {
            ++curCol;
            --curRow;
            validSpace = getValidMovesWhile(possibleMoves, board, myPosition, curRow, curCol);

        }

        validSpace = true;
        curRow = myPosition.getRow();
        curCol = myPosition.getColumn();
        // Check the right and forward spaces
        while (validSpace) {
            ++curCol;
            ++curRow;
            validSpace = getValidMovesWhile(possibleMoves, board, myPosition, curRow, curCol);
        }
        validSpace = true;
        curRow = myPosition.getRow();
        curCol = myPosition.getColumn();
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
