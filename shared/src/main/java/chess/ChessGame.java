package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();
    private Collection<ChessMove> allMoves = new ArrayList<>(); //Used to keep track of all the moves in the game
    protected ChessPosition whiteKingPos = new ChessPosition(1, 5);
    protected ChessPosition blackKingPos = new ChessPosition(8, 5);

    public ChessGame() {
        board.resetBoard();
    }
    public ChessGame(ChessBoard newBoard) {
        board = newBoard;
    }

    // Copy constructor
    public ChessGame(ChessGame original) {
        this.board = new ChessBoard(original.board);
        this.blackKingPos = original.blackKingPos;
        this.whiteKingPos = original.whiteKingPos;
        this.teamTurn = original.teamTurn;
        this.allMoves = original.allMoves;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> moves = new ArrayList<>();
        if (piece == null) return List.of();
        TeamColor pieceColor = piece.getTeamColor();
        moves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> movesCopy =  new ArrayList<>();
        for (ChessMove move : moves) {
            if (checkValidMove(move, pieceColor)) {
                movesCopy.add(move);
            }
        }
        return movesCopy;
    }


    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param move the move that needs to be checked.
     * @return whether the move would put the king in check or not
     */
    private boolean checkValidMove(ChessMove move, TeamColor teamColor) {
        ChessGame tempGame = new ChessGame(this);
        //Move the piece in the tempGame to check if it would cause check
        try { tempGame.movePiece(move); }
        catch (InvalidMoveException i) { return false;}
        return !tempGame.isInCheck(teamColor);
    }

    //Move the piece without the checks
    protected void movePiece(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPiece piece = board.getPiece(start);
        if (piece == null) throw new InvalidMoveException("No Piece at start Position");
        if(move.getPromotionPiece() != null) {//Upgrade if promotion piece
            board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        } else {
            if (piece.getPieceType() == ChessPiece.PieceType.KING) updateKingPosition(move.getEndPosition());
            board.addPiece(move.getEndPosition(), piece);
        }
        board.addPiece(start, null); //Clear old space
        if (teamTurn == TeamColor.WHITE) { teamTurn = TeamColor.BLACK; } //Switch turns
        else {teamTurn = TeamColor.WHITE; }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPiece piece = board.getPiece(start);
        if (piece == null) throw new InvalidMoveException("No Piece at start space");
        Collection<ChessMove> possibleMoves = validMoves(start);
        //Check if the move is for a piece with the correct color
        if (teamTurn != piece.getTeamColor()) { throw new InvalidMoveException("Wrong Color's Turn"); }
        if (possibleMoves.isEmpty()) {throw new InvalidMoveException("No Possible Moves");}
        if (possibleMoves.contains(move)) {
            try{movePiece(move);}
            catch (InvalidMoveException e) {throw new InvalidMoveException("No Piece at Start");}
            allMoves.add(move);
        } else {
            throw new InvalidMoveException("Move would put king in danger");
        }
    }

    private void updateKingPosition(ChessPosition pos) {
        if (teamTurn == TeamColor.WHITE) {
            whiteKingPos = pos;
        } else {
            blackKingPos = pos;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition;
        if (teamColor == TeamColor.WHITE) kingPosition = whiteKingPos;
        else kingPosition = blackKingPos;

        for (int i=1; i<=8; i++ ) { // Check row by row
            for (int j=1; j<=8; j++) { // Check columns
                ChessPosition space = new ChessPosition(i,j);
                if (board.isSpaceEmpty(space)) {
                     continue;
                }

                ChessPiece piece = board.getPiece(space);
                if (piece.getTeamColor() == teamColor) continue;
                Collection<ChessMove> attacks = piece.attackMoves(board, space);
                for (ChessMove move : attacks) {
                    if (move.getEndPosition().equals(kingPosition)) { //Compare each of the attacking end points to the king position
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        boolean inCheckmate = false;
        if (getTeamTurn() != teamColor) return false; //Can only be in checkmate on your turn
        if (!isInCheck(teamColor)) return false; // Can only be in checkmate if check first.
        //Can only be in checkmate if no valid moves for all pieces on the teamColor team
        for (int i=1; i<=8; i++ ) { // Check row by row
            for (int j=1; j<=8; j++) { // Check columns
                ChessPiece piece =  board.getPiece(new ChessPosition(i, j));
                if (board.isSpaceEmpty(new ChessPosition(i,j))) continue; //Check empty space
                if (piece.getTeamColor() != teamColor) continue; //Check if opposite team

                if (!validMoves(new ChessPosition(i, j)).isEmpty()) return false;

            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        for (int i = 1; i <= 8; i++) { // Check row by row
            for (int j = 1; j <= 8; j++) { // Check columns
                ChessPosition curPos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(curPos);
                if (board.isSpaceEmpty(curPos)) {
                    continue;
                }
                if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                    if (piece.getTeamColor() == TeamColor.WHITE) {
                        whiteKingPos = curPos;
                    } else {
                        blackKingPos = curPos;
                    }
                }
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board) && Objects.equals(allMoves, chessGame.allMoves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board, allMoves);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", board=" + board +
                '}';
    }
}
