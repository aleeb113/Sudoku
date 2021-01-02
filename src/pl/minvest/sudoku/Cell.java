package pl.minvest.sudoku;

public class Cell {

    private int value;
    private boolean changeable;

    public Cell() {
        this.value = 0;
        this.changeable = false;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isChangeable() {
        return changeable;
    }

    public void setChangeable(boolean changeable) {
        this.changeable = changeable;
    }

    @Override
    public String toString() {
        return String.valueOf(value)+changeable;
    }
}
