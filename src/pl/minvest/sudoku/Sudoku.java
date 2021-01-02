package pl.minvest.sudoku;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Sudoku {

    private Cell[] board;

    public Sudoku() {
        this.board = new Cell[81];
        Location.setCellsPositions();
        fillBoard();
    }

    public void fillBoard() {
        do {
            resetBoard();
            for (int i = 0; i < 81; i++) {
                int cellNumber = findCellToSetValue();
                setCellValue(cellNumber);
            }
        } while (findEmptyCells().size() > 0);
    }

    private void resetBoard() {
        for (int i = 0; i < 81; i++) board[i] = new Cell();
    }

    private void setCellValue(int cellNumber) {
        Random random = new Random();
        ArrayList<Integer> possibleValues = getPossibleValues(cellNumber);
        if (possibleValues.size() > 0)
            board[cellNumber].setValue(possibleValues.get(random.nextInt(possibleValues.size())));
    }

    private int findCellToSetValue() {                          //least filling options
        int[] fillingOptions = getFillingOptionsForEmptyCells();
        int leastOptions = 10;                                  //more than max
        int cellNumber = 0;
        for (int i = 0; i < fillingOptions.length; i++) {
            if (fillingOptions[i] > 0) {
                if (fillingOptions[i] < leastOptions && board[i].getValue() == 0) {
                    leastOptions = fillingOptions[i];
                    cellNumber = i;
                }
            }
        }
        return cellNumber;
    }

    private int[] getFillingOptionsForEmptyCells() {
        int[] fillingOptions = new int[81];
        for (int i = 0; i < 81; i++)
            if (board[i].getValue() == 0) fillingOptions[i] = getPossibleValues(i).size();
        return fillingOptions;
    }

    @NotNull
    private ArrayList<Integer> getPossibleValues(int cellNumber) {

        int sectionNr = Location.positions[cellNumber].getSectionNr();
        int rowNr = Location.positions[cellNumber].getRowNr();
        int columnNr = Location.positions[cellNumber].getColumnNr();

        ArrayList<Integer> possibleValues = new ArrayList<>();
        for (int i = 1; i < 10; i++) possibleValues.add(i);

        Set<Integer> toRemove = new TreeSet<>();

        for (int i : Location.section[sectionNr]) toRemove.add(board[i].getValue());
        for (int i : Location.row[rowNr]) toRemove.add(board[i].getValue());
        for (int i : Location.column[columnNr]) toRemove.add(board[i].getValue());

        possibleValues.removeAll(toRemove);
        return possibleValues;
    }


    private ArrayList<Integer> findEmptyCells() {
        ArrayList<Integer> emptyCells = new ArrayList<>();
        for (int i = 0; i < 81; i++)
            if (board[i].getValue() == 0) emptyCells.add(i);
        return emptyCells;
    }

    public static void printBoard(Cell[] board) {
        System.out.println();
        for (int i = 0; i < board.length; i++) {
            System.out.print("  " + board[i].getValue());
            if ((i + 1) % 9 == 0) System.out.println();
            else if ((i + 1) % 3 == 0) System.out.print(" ");
            if ((i + 1) % 27 == 0) System.out.println();
        }
        System.out.println();
    }

    public Cell[] getBoard() {
        return board;
    }

    public Cell[] getBoardToPlay(int emptyCellsCount) {
        Random random = new Random();
        int cellNr;
        Cell[] boardToPlay = new Cell[81];
        for (int i = 0; i < 81; i++) {
            boardToPlay[i] = new Cell();
            boardToPlay[i].setValue(board[i].getValue());
        }
        for (int i = 0; i < emptyCellsCount; i++) {
            do {
                cellNr = random.nextInt(81);
            } while (boardToPlay[cellNr].getValue() == 0);
            boardToPlay[cellNr].setValue(0);
            boardToPlay[cellNr].setChangeable(true);
        }
        return boardToPlay;
    }

    public static ErrorInfo isBoardCorrect(Cell[] sudokuBoard) {
        for (Cell cell : sudokuBoard)
            if (cell.getValue() == 0) {
                return null;
            }

        ErrorInfo errorInfo = new ErrorInfo();
        for (int[] cellNumbers : Location.section)
            checkSet(sudokuBoard, errorInfo, cellNumbers);
        for (int[] cellNumbers : Location.column)
            checkSet(sudokuBoard, errorInfo, cellNumbers);
        for (int[] cellNumbers : Location.row)
            checkSet(sudokuBoard, errorInfo, cellNumbers);

        return errorInfo;
    }

    private static void checkSet(Cell[] sudokuBoard, ErrorInfo errorInfo, int[] inputSet) {
        ArrayList<Integer> correctSet = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        List<Integer> wrongValues = new ArrayList<>();
        int cellValue;
        for (int cellNumber : inputSet) {
            cellValue = sudokuBoard[cellNumber].getValue();
            if (correctSet.contains(cellValue))
                correctSet.remove(Integer.valueOf(cellValue));
            else {
                wrongValues.add(cellValue);
                errorInfo.setCorrect(false);
            }
        }
        for (int wrongValue : wrongValues)
            for (int cellNumber : inputSet) {
                cellValue = sudokuBoard[cellNumber].getValue();
                if (cellValue == wrongValue)
                    errorInfo.getCellsNumbersWithPossiblyWrongValues().add(cellNumber);
            }
    }
}
