package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class SudokuGame extends JFrame {
    private SudokuCell[][] cells;
    private JButton newGameBtn, resetBtn, showSolutionBtn;
    private JComboBox<String> levelSelector;
    private SudokuSolver solver;
    private int[][] solution;
    private int[][] currentPuzzle;
    private Random random;

    public SudokuGame() {
        super("Sudoku");
        solver = new SudokuSolver();
        random = new Random();
        currentPuzzle = new int[9][9];
        initializeUI();
        createNewGame();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        levelSelector = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        newGameBtn = new JButton("New");
        resetBtn = new JButton("Reset");
        showSolutionBtn = new JButton("Show Solution");

        controlPanel.add(new JLabel("Level: "));
        controlPanel.add(levelSelector);
        controlPanel.add(newGameBtn);
        controlPanel.add(resetBtn);
        controlPanel.add(showSolutionBtn);

        JPanel gridPanel = new JPanel(new GridLayout(9, 9));
        cells = new SudokuCell[9][9];

        JPanel mainGrid = new JPanel(new GridLayout(3, 3, 2, 2));
        mainGrid.setBackground(Color.BLACK);

        for (int block = 0; block < 9; block++) {
            JPanel subGrid = new JPanel(new GridLayout(3, 3));
            int startRow = (block / 3) * 3;
            int startCol = (block % 3) * 3;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    int row = startRow + i;
                    int col = startCol + j;
                    cells[row][col] = new SudokuCell(row, col, this);
                    cells[row][col].setPreferredSize(new Dimension(50, 50));
                    subGrid.add(cells[row][col]);
                }
            }
            mainGrid.add(subGrid);
        }

        add(controlPanel, BorderLayout.NORTH);
        add(mainGrid, BorderLayout.CENTER);

        newGameBtn.addActionListener(e -> createNewGame());
        resetBtn.addActionListener(e -> resetGame());
        showSolutionBtn.addActionListener(e -> showSolution());

        pack();
        setLocationRelativeTo(null);
    }

    private void generateRandomSolution() {
        //Base grid
        int[][] base = {
                {1,2,3,4,5,6,7,8,9},
                {4,5,6,7,8,9,1,2,3},
                {7,8,9,1,2,3,4,5,6},
                {2,3,1,5,6,4,8,9,7},
                {5,6,4,8,9,7,2,3,1},
                {8,9,7,2,3,1,5,6,4},
                {3,1,2,6,4,5,9,7,8},
                {6,4,5,9,7,8,3,1,2},
                {9,7,8,3,1,2,6,4,5}
        };

        for (int block = 0; block < 3; block++) {
            ArrayList<int[]> rows = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                rows.add(base[block * 3 + i].clone());
            }
            Collections.shuffle(rows);
            for (int i = 0; i < 3; i++) {
                base[block * 3 + i] = rows.get(i);
            }
        }

        int[][] temp = new int[9][9];
        for (int block = 0; block < 3; block++) {
            ArrayList<Integer> cols = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                cols.add(block * 3 + i);
            }
            Collections.shuffle(cols);
            for (int row = 0; row < 9; row++) {
                for (int i = 0; i < 3; i++) {
                    temp[row][cols.get(i)] = base[row][block * 3 + i];
                }
            }
        }

        ArrayList<Integer> blockOrder = new ArrayList<>();
        for (int i = 0; i < 3; i++) blockOrder.add(i);
        Collections.shuffle(blockOrder);

        solution = new int[9][9];
        for (int block = 0; block < 3; block++) {
            for (int row = 0; row < 3; row++) {
                System.arraycopy(temp[blockOrder.get(block) * 3 + row], 0,
                        solution[block * 3 + row], 0, 9);
            }
        }

        if (random.nextBoolean()) {
            for (int i = 0; i < 9; i++) {
                for (int j = i + 1; j < 9; j++) {
                    int t = solution[i][j];
                    solution[i][j] = solution[j][i];
                    solution[j][i] = t;
                }
            }
        }

        if (random.nextBoolean()) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 4; j++) {
                    int t = solution[i][j];
                    solution[i][j] = solution[i][8-j];
                    solution[i][8-j] = t;
                }
            }
        }

        ArrayList<Integer> numberMapping = new ArrayList<>();
        for (int i = 1; i <= 9; i++) numberMapping.add(i);
        Collections.shuffle(numberMapping);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                solution[i][j] = numberMapping.get(solution[i][j] - 1);
            }
        }
    }

    public boolean isValidMove(int row, int col, int value) {
        for (int j = 0; j < 9; j++) {
            if (j != col && !cells[row][j].getText().isEmpty() &&
                    Integer.parseInt(cells[row][j].getText()) == value) {
                return false;
            }
        }

        for (int i = 0; i < 9; i++) {
            if (i != row && !cells[i][col].getText().isEmpty() &&
                    Integer.parseInt(cells[i][col].getText()) == value) {
                return false;
            }
        }

        int boxRow = row - row % 3;
        int boxCol = col - col % 3;
        for (int i = boxRow; i < boxRow + 3; i++) {
            for (int j = boxCol; j < boxCol + 3; j++) {
                if ((i != row || j != col) && !cells[i][j].getText().isEmpty() &&
                        Integer.parseInt(cells[i][j].getText()) == value) {
                    return false;
                }
            }
        }

        return value == solution[row][col];
    }

    private void createNewGame() {
        generateRandomSolution();

        int numbersToKeep;
        String level = (String) levelSelector.getSelectedItem();
        //Level select hiding cells
        if (level.equals("Easy")) {
            numbersToKeep = random.nextInt(11) + 35;
        } else if (level.equals("Medium")) {
            numbersToKeep = random.nextInt(11) + 30;
        } else {
            numbersToKeep = random.nextInt(11) + 25;
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j].setText("");
                cells[i][j].setModifiable(true);
                cells[i][j].setBackground(Color.WHITE);
                currentPuzzle[i][j] = 0;
            }
        }

        ArrayList<Point> positions = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                positions.add(new Point(i, j));
            }
        }
        Collections.shuffle(positions);

        for (int i = 0; i < numbersToKeep; i++) {
            Point pos = positions.get(i);
            int row = pos.x;
            int col = pos.y;
            cells[row][col].setText(String.valueOf(solution[row][col]));
            cells[row][col].setModifiable(false);
            currentPuzzle[row][col] = solution[row][col];
        }

        resetBtn.setEnabled(true);
    }

    //Clearing all user inputs
    private void resetGame() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (cells[i][j].isModifiable()) {
                    cells[i][j].setText("");
                    cells[i][j].setBackground(Color.WHITE);
                }
            }
        }
    }

    //Show solution
    private void showSolution() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j].setText(String.valueOf(solution[i][j]));
                cells[i][j].setModifiable(false);
                cells[i][j].setBackground(new Color(240, 240, 240));
            }
        }
        resetBtn.setEnabled(false);
    }
}