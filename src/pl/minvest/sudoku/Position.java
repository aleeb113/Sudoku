package pl.minvest.sudoku;

public class Position {

private int sectionNr, rowNr, columnNr;

    public Position(int sectionNr, int rowNr, int columnNr) {
        this.sectionNr = sectionNr;
        this.rowNr = rowNr;
        this.columnNr = columnNr;
    }

    @Override
    public String toString() {
        return "Position{" +
                "section = " + this.sectionNr +
                ", row = " + this.rowNr +
                ", column = " + this.columnNr +
                '}';
    }

    public int getSectionNr() {
        return sectionNr;
    }

    public int getRowNr() {
        return rowNr;
    }

    public int getColumnNr() {
        return columnNr;
    }
}
