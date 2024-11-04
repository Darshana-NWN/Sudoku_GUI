package org.example;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class SudokuCell extends JTextField {
    private boolean isModifiable;
    private int row;
    private int col;
    private SudokuGame game;

    public SudokuCell(int row, int col, SudokuGame game) {
        super();
        this.row = row;
        this.col = col;
        this.game = game;
        this.setHorizontalAlignment(JTextField.CENTER);
        this.setFont(new Font("Arial", Font.BOLD, 20));
        this.setDocument(new NumberDocument());

        this.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateInput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (getText().isEmpty()) {
                    setBackground(Color.WHITE);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateInput();
            }
        });
    }

    private void validateInput() {
        if (!getText().isEmpty() && isModifiable) {
            int value = Integer.parseInt(getText());
            if (!game.isValidMove(row, col, value)) {
                setBackground(Color.PINK);
            } else {
                setBackground(Color.WHITE);
            }
        }
    }

    public boolean isModifiable() {
        return isModifiable;
    }

    public void setModifiable(boolean modifiable) {
        isModifiable = modifiable;
        this.setEditable(modifiable);
        if (!modifiable) {
            this.setBackground(new Color(240, 240, 240));
        } else {
            this.setBackground(Color.WHITE);
        }
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}