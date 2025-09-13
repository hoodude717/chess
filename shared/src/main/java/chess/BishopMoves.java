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

        // Check the left and forward spaces
        while (validSpace) {
            --curCol;
            ++curRow;
            validSpace = getValidMovesWhile(possibleMoves, board, myPosition, curRow, curCol);
        }


        validSpace = true;
        curRow = myPosition.getRow();
        curCol = myPosition.getColumn();
        // Check the left and behind spaces
        while (validSpace) {
            --curCol;
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
        // Check the right and behind spaces
        while (validSpace) {
            ++curCol;
            --curRow;
            validSpace = getValidMovesWhile(possibleMoves, board, myPosition, curRow, curCol);
        }


        return possibleMoves;
    }
}
