package pl.minvest.sudoku;

import java.util.ArrayList;

public class ErrorInfo {

    private boolean correct;

    private ArrayList<Integer> cellsNumbersWithPossiblyWrongValues;

    public ErrorInfo() {
        this.correct = true;
        cellsNumbersWithPossiblyWrongValues = new ArrayList<>();
    }
    public boolean isCorrect() {
        return correct;
    }
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
    public ArrayList<Integer> getCellsNumbersWithPossiblyWrongValues() {
        return cellsNumbersWithPossiblyWrongValues;
    }


}
