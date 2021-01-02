package pl.minvest.sudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SudokuFrame extends JFrame {

    private JPanel mainPanel;
    private Rectangle2D.Double[] squares;
    private int selectedCellNumber;

    private Cell[] sudokuBoard;
    private ErrorInfo errorInfo;

    public SudokuFrame(Cell[] sudokuBoard) throws HeadlessException {

        this.sudokuBoard = sudokuBoard;
        this.mainPanel = new JPanel();
        this.squares = new Rectangle2D.Double[81];
        this.mainPanel.add(new SudokuComponent(this.sudokuBoard));
        JPanel infoPanel = new JPanel();

        JButton checkButton = new JButton("Check");
        checkButton.addActionListener(actionEvent -> {
            String message;
            this.errorInfo = Sudoku.isBoardCorrect(this.sudokuBoard);
            if (errorInfo == null) message = "Fill the board first";
            else if (errorInfo.isCorrect()) message = "Excellent! Good job!";
            else message = "Not correct. Try to fix it.";
            JOptionPane.showMessageDialog(getParent(), message);
            pack();
            repaint();
        });

        JButton newSudoku = new JButton("Create New Sudoku Board ");
        newSudoku.addActionListener(actionEvent -> {
            String[] sudokuOptions = {"Easy", "Medium", "Hard", "Expert"};
            int selectedSudokuLevel = JOptionPane.showOptionDialog(null, "Select difficulty level", "new Sudoku Level",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, sudokuOptions, sudokuOptions[0]);
            if (selectedSudokuLevel == 0) printNewSudoku(31);
            else if (selectedSudokuLevel == 1) printNewSudoku(41);
            else if (selectedSudokuLevel == 2) printNewSudoku(51);
            else if (selectedSudokuLevel == 3) printNewSudoku(58);
        });

        infoPanel.add(checkButton);
        infoPanel.add(newSudoku);
        add(this.mainPanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.SOUTH);
        pack();
        repaint();
    }

    private void printNewSudoku(int emptyCellsCount) {
        this.errorInfo = new ErrorInfo();
        this.sudokuBoard = new Sudoku().getBoardToPlay(emptyCellsCount);
        this.mainPanel.removeAll();
        this.mainPanel.add(new SudokuComponent(sudokuBoard));
        pack();
        repaint();
    }

    private class SudokuComponent extends JComponent {
        private final int cellSize;
        private Cell[] board;

        public SudokuComponent(Cell[] sudokuBoard) {
            this.board = sudokuBoard;
            selectedCellNumber = -1; //no cell is selected yet
            cellSize = SudokuConfig.CELL_SIZE;

            SudokuMouseListener sudokuMouseListener = new SudokuMouseListener();
            addMouseListener(sudokuMouseListener);

            setActionsForInputDigits();
        }

        private void setActionsForInputDigits() {
            InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

            inputMap.put(KeyStroke.getKeyStroke("1"), "putOne");
            inputMap.put(KeyStroke.getKeyStroke("2"), "putTwo");
            inputMap.put(KeyStroke.getKeyStroke("3"), "putThree");
            inputMap.put(KeyStroke.getKeyStroke("4"), "putFour");
            inputMap.put(KeyStroke.getKeyStroke("5"), "putFive");
            inputMap.put(KeyStroke.getKeyStroke("6"), "putSix");
            inputMap.put(KeyStroke.getKeyStroke("7"), "putSeven");
            inputMap.put(KeyStroke.getKeyStroke("8"), "putEight");
            inputMap.put(KeyStroke.getKeyStroke("9"), "putNine");
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "delete");

            ActionMap actionMap = getActionMap();

            Action oneAction = new putValueAction(1);
            Action twoAction = new putValueAction(2);
            Action threeAction = new putValueAction(3);
            Action fourAction = new putValueAction(4);
            Action fiveAction = new putValueAction(5);
            Action sixAction = new putValueAction(6);
            Action sevenAction = new putValueAction(7);
            Action eightAction = new putValueAction(8);
            Action nineAction = new putValueAction(9);
            Action deleteAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (selectedCellNumber >= 0) {
                        board[selectedCellNumber].setValue(0);
                        selectedCellNumber = -1;
                        repaint();
                    }
                }
            };
            actionMap.put("putOne", oneAction);
            actionMap.put("putTwo", twoAction);
            actionMap.put("putThree", threeAction);
            actionMap.put("putFour", fourAction);
            actionMap.put("putFive", fiveAction);
            actionMap.put("putSix", sixAction);
            actionMap.put("putSeven", sevenAction);
            actionMap.put("putEight", eightAction);
            actionMap.put("putNine", nineAction);
            actionMap.put("delete", deleteAction);
        }

        @Override
        public Dimension getPreferredSize() {
            return SudokuConfig.SUDOKU_COMPONENT_SIZE;
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2D = (Graphics2D) g;
            g2D.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
            Stroke stroke = new BasicStroke(1);
            g2D.setStroke(stroke);


            int cellNumber = 0;
            for (int y = 0; y < 9; y++)
                for (int x = 0; x < 9; x++) {
                    Rectangle2D.Double square = new Rectangle2D.Double(SudokuConfig.GAP + x * cellSize, SudokuConfig.GAP + y * cellSize, cellSize, cellSize);
                    squares[cellNumber] = square;

                    if (!board[cellNumber].isChangeable()) {
                        printLittleSquare(g2D, square, Color.LIGHT_GRAY);
                        printCellValue(g2D, board[cellNumber].getValue(), x, y);
                    } else {
                        printLittleSquare(g2D, square, Color.WHITE);
                        if (board[cellNumber].getValue() != 0) {
                            printCellValue(g2D, board[cellNumber].getValue(), x, y);
                        }

                    }
                    if (cellNumber == selectedCellNumber) {
                        printLittleSquare(g2D, square, Color.ORANGE);
                        if (board[cellNumber].getValue() != 0) {
                            printCellValue(g2D, board[cellNumber].getValue(), x, y);
                        }
                    }
                    cellNumber++;
                }
            if (errorInfo != null) printErrorHints(g2D);
            if (selectedCellNumber >= 0) printHints(g2D);
            paintBigSquares(g2D);
        }

        private void printHints(Graphics2D g2D) {
            Map<Integer, Point> cellsCoordinates = new HashMap<>();
            getCellsCoordinates(cellsCoordinates);

            Set<Integer> numbersOfCellsToPrint = new HashSet<>();

            int sectionNumber = Location.positions[selectedCellNumber].getSectionNr();
            int rowNumber = Location.positions[selectedCellNumber].getRowNr();
            int columnNumber = Location.positions[selectedCellNumber].getColumnNr();

            for (int cellNumber : Location.section[sectionNumber]) numbersOfCellsToPrint.add(cellNumber);
            for (int cellNumber : Location.row[rowNumber]) numbersOfCellsToPrint.add(cellNumber);
            for (int cellNumber : Location.column[columnNumber]) numbersOfCellsToPrint.add(cellNumber);

            for (int cellNumber : numbersOfCellsToPrint) {
                Point cellCoordinates = cellsCoordinates.get(cellNumber);
                int x = cellCoordinates.x;
                int y = cellCoordinates.y;
                Rectangle2D.Double square = new Rectangle2D.Double(SudokuConfig.GAP + x * cellSize, SudokuConfig.GAP + y * cellSize, cellSize, cellSize);
                if (selectedCellNumber == cellNumber) printLittleSquare(g2D, square, Color.ORANGE);
                else printLittleSquare(g2D, square, Color.YELLOW);
                if (board[cellNumber].getValue() != 0) printCellValue(g2D, board[cellNumber].getValue(), x, y);
            }
        }

        private void printErrorHints(Graphics2D g2D) {

            Set<Integer> cellsNumbersToPrint = new HashSet<>(errorInfo.getCellsNumbersWithPossiblyWrongValues());

            Map<Integer, Point> cellsCoordinates = new HashMap<>();
            getCellsCoordinates(cellsCoordinates);

            for (int cellNumber : cellsNumbersToPrint) {
                Point cellCoordinates = cellsCoordinates.get(cellNumber);
                int x = cellCoordinates.x;
                int y = cellCoordinates.y;
                System.out.println("cell to print => " + cellNumber + " " + cellCoordinates);
                Rectangle2D.Double square = new Rectangle2D.Double(SudokuConfig.GAP + x * cellSize, SudokuConfig.GAP + y * cellSize, cellSize, cellSize);
                printLittleSquare(g2D, square, Color.PINK);
                if (board[cellNumber].getValue() > 0) printCellValue(g2D, board[cellNumber].getValue(), x, y);
            }
            errorInfo = null;
        }

        private void getCellsCoordinates(Map<Integer, Point> cellsCoordinates) {
            int cellNumber = 0;
            for (int y = 0; y < 9; y++)
                for (int x = 0; x < 9; x++) {
                    cellsCoordinates.put(cellNumber, new Point(x, y));
                    cellNumber++;
                }
        }

        private void printCellValue(Graphics2D g2D, int value, int x, int y) {
            g2D.drawString(String.valueOf(value),
                    SudokuConfig.GAP + x * cellSize + SudokuConfig.SHIFT_X,
                    SudokuConfig.GAP + y * cellSize + SudokuConfig.SHIFT_Y);
        }

        private class putValueAction extends AbstractAction {
            private int inputValue;

            public putValueAction(int inputValue) {
                this.inputValue = inputValue;
            }

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (selectedCellNumber >= 0) {
                    sudokuBoard[selectedCellNumber].setValue(inputValue);
                    selectedCellNumber = -1;
                    repaint();
                }
            }
        }
    }

    private void printLittleSquare(Graphics2D g2D, Rectangle2D.Double square, Color color) {
        g2D.setColor(color);
        g2D.fill(square);
        g2D.setColor(SudokuConfig.THEME_COLOR);
        g2D.draw(square);
    }

    private void paintBigSquares(Graphics2D g2D) {
        Stroke stroke = new BasicStroke(5);
        int cellSize = SudokuConfig.CELL_SIZE * 3;
        g2D.setColor(Color.BLACK);
        for (int x = 0; x < 3; x++)
            for (int y = 0; y < 3; y++) {
                Rectangle2D rec = new Rectangle2D.Double(SudokuConfig.GAP + x * cellSize,
                        SudokuConfig.GAP + y * cellSize, cellSize, cellSize);
                g2D.setStroke(stroke);
                g2D.draw(rec);
            }
    }

    private class SudokuMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            Point point = e.getPoint();
            for (int cellNumber = 0; cellNumber < 81; cellNumber++)
                if (squares[cellNumber].contains(point) && sudokuBoard[cellNumber].isChangeable()) {
                    selectedCellNumber = cellNumber;
                    repaint();
                    break;
                }

        }
    }
}












