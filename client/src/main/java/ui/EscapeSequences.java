package ui;

import chess.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static chess.ChessPiece.PieceType.BLANK;

/**
 * This class contains constants and functions relating to ANSI Escape Sequences that are useful in the Client display
 */
public class EscapeSequences {


    private static final String UNICODE_ESCAPE = "\u001b";
    private static final String ANSI_ESCAPE = "\033";
    public static final String FLAG_USA = "🇺🇸";
    public static final String FLAG_UK = "🇬🇧";
    public static final String FLAG_MEXICO = "🇲🇽";
    public static final String FLAG_DR = "🇩🇴";


    public static final String ERASE_SCREEN = UNICODE_ESCAPE + "[H" + UNICODE_ESCAPE + "[2J";
    public static final String ERASE_LINE = UNICODE_ESCAPE + "[2K";


    public static final String SET_TEXT_BOLD = UNICODE_ESCAPE + "[1m";
    public static final String SET_TEXT_FAINT = UNICODE_ESCAPE + "[2m";
    public static final String RESET_TEXT_BOLD_FAINT = UNICODE_ESCAPE + "[22m";
    public static final String SET_TEXT_ITALIC = UNICODE_ESCAPE + "[3m";
    public static final String RESET_TEXT_ITALIC = UNICODE_ESCAPE + "[23m";
    public static final String SET_TEXT_UNDERLINE = UNICODE_ESCAPE + "[4m";
    public static final String RESET_TEXT_UNDERLINE = UNICODE_ESCAPE + "[24m";
    public static final String SET_TEXT_BLINKING = UNICODE_ESCAPE + "[5m";
    public static final String RESET_TEXT_BLINKING = UNICODE_ESCAPE + "[25m";

    private static final String SET_TEXT_COLOR = UNICODE_ESCAPE + "[38;5;";
    private static final String SET_BG_COLOR = UNICODE_ESCAPE + "[48;5;";

    public static final String SET_TEXT_COLOR_BLACK = SET_TEXT_COLOR + "0m";
    public static final String SET_TEXT_COLOR_LIGHT_GREY = SET_TEXT_COLOR + "242m";
    public static final String SET_TEXT_COLOR_DARK_GREY = SET_TEXT_COLOR + "235m";
    public static final String SET_TEXT_COLOR_RED = SET_TEXT_COLOR + "160m";
    public static final String SET_TEXT_COLOR_GREEN = SET_TEXT_COLOR + "46m";
    public static final String SET_TEXT_COLOR_YELLOW = SET_TEXT_COLOR + "226m";
    public static final String SET_TEXT_COLOR_BLUE = SET_TEXT_COLOR + "12m";
    public static final String SET_TEXT_COLOR_MAGENTA = SET_TEXT_COLOR + "5m";
    public static final String SET_TEXT_COLOR_WHITE = SET_TEXT_COLOR + "15m";
    public static final String RESET_TEXT_COLOR = UNICODE_ESCAPE + "[39m";
    public static final String SET_TEXT_COLOR_DARK_BROWN = SET_TEXT_COLOR + "52m";
    public static final String SET_TEXT_COLOR_DARK_GREEN = SET_TEXT_COLOR + "22m";
    public static final String SET_TEXT_COLOR_TAN = SET_TEXT_COLOR + "180m";


    public static final String SET_BG_COLOR_BLACK = SET_BG_COLOR + "0m";
    public static final String SET_BG_COLOR_LIGHT_GREY = SET_BG_COLOR + "242m";
    public static final String SET_BG_COLOR_DARK_GREY = SET_BG_COLOR + "235m";
    public static final String SET_BG_COLOR_RED = SET_BG_COLOR + "160m";
    public static final String SET_BG_COLOR_GREEN = SET_BG_COLOR + "46m";
    public static final String SET_BG_COLOR_DARK_GREEN = SET_BG_COLOR + "22m";
    public static final String SET_BG_COLOR_YELLOW = SET_BG_COLOR + "226m";
    public static final String SET_BG_COLOR_BLUE = SET_BG_COLOR + "12m";
    public static final String SET_BG_COLOR_MAGENTA = SET_BG_COLOR + "5m";
    public static final String SET_BG_COLOR_WHITE = SET_BG_COLOR + "15m";
    public static final String SET_BG_COLOR_BROWN = SET_BG_COLOR + "94m";
    public static final String SET_BG_COLOR_DARK_BROWN = SET_BG_COLOR + "52m";
    public static final String SET_BG_COLOR_TAN = SET_BG_COLOR + "180m";
    public static final String SET_BG_COLOR_BEIGE = SET_BG_COLOR + "230m";
    public static final String SET_BG_COLOR_ORANGE = SET_BG_COLOR + "208m";
    public static final String SET_BG_COLOR_DARK_ORANGE = SET_BG_COLOR + "166m";
    public static final String SET_BG_COLOR_PURPLE = SET_BG_COLOR + "93m";
    public static final String SET_BG_COLOR_DARK_PURPLE = SET_BG_COLOR + "54m";
    public static final String SET_BG_COLOR_PINK = SET_BG_COLOR + "218m";
    public static final String SET_BG_COLOR_HOT_PINK = SET_BG_COLOR + "198m";
    public static final String SET_BG_COLOR_CYAN = SET_BG_COLOR + "51m";
    public static final String SET_BG_COLOR_DARK_CYAN = SET_BG_COLOR + "30m";
    public static final String SET_BG_COLOR_TEAL = SET_BG_COLOR + "38m";
    public static final String SET_BG_COLOR_LIGHT_BLUE = SET_BG_COLOR + "117m";
    public static final String SET_BG_COLOR_NAVY = SET_BG_COLOR + "17m";
    public static final String SET_BG_COLOR_SKY_BLUE = SET_BG_COLOR + "87m";
    public static final String SET_BG_COLOR_LIME = SET_BG_COLOR + "118m";
    public static final String SET_BG_COLOR_FOREST_GREEN = SET_BG_COLOR + "28m";
    public static final String SET_BG_COLOR_OLIVE = SET_BG_COLOR + "100m";
    public static final String SET_BG_COLOR_DARK_RED = SET_BG_COLOR + "88m";
    public static final String SET_BG_COLOR_CRIMSON = SET_BG_COLOR + "124m";
    public static final String SET_BG_COLOR_MAROON = SET_BG_COLOR + "52m";
    public static final String SET_BG_COLOR_GRAY_236 = SET_BG_COLOR + "236m";
    public static final String SET_BG_COLOR_GRAY_238 = SET_BG_COLOR + "238m";
    public static final String SET_BG_COLOR_GRAY_240 = SET_BG_COLOR + "240m";
    public static final String SET_BG_COLOR_GRAY_244 = SET_BG_COLOR + "244m";
    public static final String SET_BG_COLOR_GRAY_248 = SET_BG_COLOR + "248m";
    public static final String SET_BG_COLOR_CREAM = SET_BG_COLOR + "230m";
    public static final String SET_BG_COLOR_SAND = SET_BG_COLOR + "223m";
    public static final String RESET_BG_COLOR = UNICODE_ESCAPE + "[49m";

    public static final String WHITE_KING = " ♔ ";
    public static final String WHITE_QUEEN = " ♕ ";
    public static final String WHITE_BISHOP = " ♗ ";
    public static final String WHITE_KNIGHT = " ♘ ";
    public static final String WHITE_ROOK = " ♖ ";
    public static final String WHITE_PAWN = " ♙ ";
    public static final String BLACK_KING = " ♚ ";
    public static final String BLACK_QUEEN = " ♛ ";
    public static final String BLACK_BISHOP = " ♝ ";
    public static final String BLACK_KNIGHT = " ♞ ";
    public static final String BLACK_ROOK = " ♜ ";
    public static final String BLACK_PAWN = " ♟ ";
    public static final String EMPTY = " \u2003 ";


    public static final String RESET = SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE;
    public static final String RESET_POST = SET_BG_COLOR_BLACK + SET_TEXT_COLOR_BLUE;
    public static final String RESET_GAME = RESET + SET_TEXT_COLOR_MAGENTA;
    public static final String LOGO = UNICODE_ESCAPE + "1F1FA" + UNICODE_ESCAPE + "1F1F8";


    public static String moveCursorToLocation(int x, int y) { return UNICODE_ESCAPE + "[" + y + ";" + x + "H"; }

    //Strings for printing the entire board
    public static final String ABCD_ROW = SET_BG_COLOR_MAROON + SET_TEXT_COLOR_RED +
            EMPTY + " a  b  c  d  e  f  g  h " + EMPTY + RESET_GAME;
    public static final String HGFE_ROW = SET_BG_COLOR_MAROON + SET_TEXT_COLOR_RED +
            EMPTY + " h  g  f  e  d  c  b  a " + EMPTY + RESET_GAME;


    private static String printPiecesByRow(List<ChessPiece> rowPieces, int itr) {
        ChessPiece.PieceType type;
        ChessGame.TeamColor color;
        StringBuilder rowString = new StringBuilder();
        if (rowPieces.get(itr) != null) {
            type = rowPieces.get(itr).getPieceType();
            color = rowPieces.get(itr).getTeamColor();
        } else {
            type = BLANK;
            color = ChessGame.TeamColor.BLACK;
        }
        if (color == ChessGame.TeamColor.WHITE) {
            rowString.append(SET_TEXT_COLOR_TAN);
            switch (type) {
                case KING -> rowString.append(WHITE_KING);
                case QUEEN -> rowString.append(WHITE_QUEEN);
                case BISHOP-> rowString.append(WHITE_BISHOP);
                case KNIGHT -> rowString.append(WHITE_KNIGHT);
                case ROOK -> rowString.append(WHITE_ROOK);
                case PAWN -> rowString.append(WHITE_PAWN);
                default -> rowString.append(EMPTY);
            }
        } else {
            rowString.append(SET_TEXT_COLOR_DARK_BROWN);
            switch (type) {
                case KING -> rowString.append(BLACK_KING);
                case QUEEN -> rowString.append(BLACK_QUEEN);
                case BISHOP-> rowString.append(BLACK_BISHOP);
                case KNIGHT -> rowString.append(BLACK_KNIGHT);
                case ROOK -> rowString.append(BLACK_ROOK);
                case PAWN -> rowString.append(BLACK_PAWN);
                default -> rowString.append(EMPTY);
            }
        }
        return rowString.toString();
    }
    // Takes in String rowNum and the pieces in that row, rowNum must be padded with one space on each side
    public static String blackSquareFirstRow(List<ChessPiece> rowPieces,  Integer rowNum, Collection<ChessMove> moves, String color) {
        String rowStr = " " + rowNum + " ";
        StringBuilder rowString = new StringBuilder(SET_BG_COLOR_MAROON + SET_TEXT_COLOR_RED + rowStr);
        for (int i = 0; i < 8; i++) {
            boolean isAMove = false;
            for (var move : moves) {
                if (color.equals("white")) {
                    if (move.getStartPosition().getColumn() == i+1 && move.getStartPosition().getRow() == rowNum) {
                        rowString.append(SET_BG_COLOR_YELLOW);
                        isAMove = true;
                        break;
                    }
                    if (move.getEndPosition().getColumn() == i+1 && move.getEndPosition().getRow() == rowNum) {
                        if (i%2 == 0) { rowString.append(SET_BG_COLOR_ORANGE); }
                        else { rowString.append(SET_BG_COLOR_DARK_ORANGE); }
                        isAMove = true;
                    }
                } else {
                    if (move.getStartPosition().getColumn() == 8-i && move.getStartPosition().getRow() == rowNum) {
                        rowString.append(SET_BG_COLOR_YELLOW);
                        isAMove = true;
                        break;
                    }
                    if (move.getEndPosition().getColumn() == 8-i && move.getEndPosition().getRow() == rowNum) {
                        if (i%2 == 0) { rowString.append(SET_BG_COLOR_ORANGE); }
                        else { rowString.append(SET_BG_COLOR_DARK_ORANGE); }
                        isAMove = true;
                    }
                }

            }
            if (!isAMove) {
                if (i%2 == 0) {
                    rowString.append(SET_BG_COLOR_BROWN);
                } else {
                    rowString.append(SET_BG_COLOR_CREAM);
                }
            }

            rowString.append(printPiecesByRow(rowPieces, i));
        }
        rowString.append(SET_BG_COLOR_MAROON + SET_TEXT_COLOR_RED).append(rowStr).append(RESET_GAME);
        return rowString.toString();
    }

    public static String whiteSquareFirstRow(List<ChessPiece> rowPieces, Integer rowNum, Collection<ChessMove> moves, String color) {
        String rowStr = " " + rowNum + " ";
        StringBuilder rowString = new StringBuilder(SET_BG_COLOR_MAROON + SET_TEXT_COLOR_RED + rowStr);

        for (int i = 0; i < 8; i++) {
            boolean isAMove = false;
            for (var move : moves) {
                if (color.equals("white")) {
                    if (move.getStartPosition().getColumn() == i+1 && move.getStartPosition().getRow() == rowNum) {
                        rowString.append(SET_BG_COLOR_YELLOW);
                        isAMove = true;
                        break;
                    }
                    if (move.getEndPosition().getColumn() == i+1 && move.getEndPosition().getRow() == rowNum) {
                        if (i%2 == 0) { rowString.append(SET_BG_COLOR_DARK_ORANGE); }
                        else { rowString.append(SET_BG_COLOR_ORANGE); }
                        isAMove = true;
                    }
                } else {
                    if (move.getStartPosition().getColumn() == 8 - i && move.getStartPosition().getRow() == rowNum) {
                        rowString.append(SET_BG_COLOR_YELLOW);
                        isAMove = true;
                        break;
                    }
                    if (move.getEndPosition().getColumn() == 8 - i && move.getEndPosition().getRow() == rowNum) {
                        if (i % 2 == 0) {
                            rowString.append(SET_BG_COLOR_DARK_ORANGE);
                        } else {
                            rowString.append(SET_BG_COLOR_ORANGE);
                        }
                        isAMove = true;
                    }
                }
            }
            if (!isAMove) {
                if (i%2 == 0) {
                    rowString.append(SET_BG_COLOR_CREAM);
                } else {
                    rowString.append(SET_BG_COLOR_BROWN);
                }
            }

            rowString.append(printPiecesByRow(rowPieces, i));

        }
        rowString.append(SET_BG_COLOR_MAROON + SET_TEXT_COLOR_RED).append(rowStr).append(RESET_GAME);
        return rowString.toString();
    }

    public static String printBoard(ChessBoard board, String color, Collection<ChessMove> moves) {
        String retStr = "\n";
        if (moves == null) {
            moves = List.of();
        }
        //Fix this to dynamically get the row not to make random rows.
        List<ChessPiece> row1 = Arrays.asList(board.getRow(1));
        List<ChessPiece> row2 = Arrays.asList(board.getRow(2));
        List<ChessPiece> row3 = Arrays.asList(board.getRow(3));
        List<ChessPiece> row4 = Arrays.asList(board.getRow(4));
        List<ChessPiece> row5 = Arrays.asList(board.getRow(5));
        List<ChessPiece> row6 = Arrays.asList(board.getRow(6));
        List<ChessPiece> row7 = Arrays.asList(board.getRow(7));
        List<ChessPiece> row8 = Arrays.asList(board.getRow(8));
        if (color.equals("white") || color.equals("blanco")) {
            retStr += ABCD_ROW + "\n";
            retStr += whiteSquareFirstRow(row8, 8, moves, color) + "\n";
            retStr += blackSquareFirstRow(row7, 7, moves, color) + "\n";
            retStr += whiteSquareFirstRow(row6, 6, moves, color) + "\n";
            retStr += blackSquareFirstRow(row5, 5, moves, color) + "\n";
            retStr += whiteSquareFirstRow(row4, 4, moves, color) + "\n";
            retStr += blackSquareFirstRow(row3, 3, moves, color) + "\n";
            retStr += whiteSquareFirstRow(row2, 2, moves, color) + "\n";
            retStr += blackSquareFirstRow(row1, 1, moves, color) + "\n";
            retStr += ABCD_ROW + "\n";
        } else {
            retStr += HGFE_ROW + "\n";
            retStr += whiteSquareFirstRow(row1.reversed(), 1, moves, color) + "\n";
            retStr += blackSquareFirstRow(row2.reversed(), 2, moves, color) + "\n";
            retStr += whiteSquareFirstRow(row3.reversed(), 3, moves, color) + "\n";
            retStr += blackSquareFirstRow(row4.reversed(), 4, moves, color) + "\n";
            retStr += whiteSquareFirstRow(row5.reversed(), 5, moves, color) + "\n";
            retStr += blackSquareFirstRow(row6.reversed(), 6, moves, color) + "\n";
            retStr += whiteSquareFirstRow(row7.reversed(), 7, moves, color) + "\n";
            retStr += blackSquareFirstRow(row8.reversed(), 8, moves, color) + "\n";
            retStr += HGFE_ROW + "\n";
        }

        return retStr;
    }


}
