package pl.minvest.sudoku;


import javax.swing.*;
import java.awt.*;

public class SudokuStarter {

    public static void main(String[] args)  {

        Sudoku sudoku = new Sudoku();
        Cell[] playBoard = sudoku.getBoardToPlay(29);

        EventQueue.invokeLater(()->{
            SudokuFrame frame = new SudokuFrame(playBoard);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });










    }


    }


