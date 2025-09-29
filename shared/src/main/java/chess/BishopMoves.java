package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMoves implements ChessMoveCalculator {

    ChessPiece piece;
    private Collection<ChessMove> attacks = new ArrayList<>();
    public BishopMoves(ChessPiece newPiece) {
        piece = newPiece;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        boolean validSpace = true;
        int curRow = myPosition.getRow();
        int curCol = myPosition.getColumn();

        // Check the left and forward spaces
        while (validSpace) {
            --curCol;
            ++curRow;
            validSpace = getValidMovesWhile(possibleMoves, board, myPosition, curRow, curCol);
        }
        ChessMove lastMove;
        if (!possibleMoves.isEmpty()) {
            lastMove = possibleMoves.getLast();
            if (!board.isSpaceEmpty(lastMove.getEndPosition())) { //Check the last space that was added and see if there is a piece there
                attacks.add(lastMove);
            }
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
        if (!possibleMoves.isEmpty()) {
            lastMove = possibleMoves.getLast();
            if (!board.isSpaceEmpty(lastMove.getEndPosition())) { //Check the last space that was added and see if there is a piece there
                attacks.add(lastMove);
            }
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
        if (!possibleMoves.isEmpty()) {
            lastMove = possibleMoves.getLast();
            if (!board.isSpaceEmpty(lastMove.getEndPosition())) { //Check the last space that was added and see if there is a piece there
                attacks.add(lastMove);
            }
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
        if (!possibleMoves.isEmpty()) {
            lastMove = possibleMoves.getLast();
            if (!board.isSpaceEmpty(lastMove.getEndPosition())) { //Check the last space that was added and see if there is a piece there
                attacks.add(lastMove);
            }
        }


        return possibleMoves;
    }

    @Override
    public Collection<ChessMove> attackMoves(ChessBoard board, ChessPosition myPosition) {
        pieceMoves(board, myPosition);
        return attacks;
    }
}
