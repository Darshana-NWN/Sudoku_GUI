package org.example;

import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

public class NumberDocument extends PlainDocument {
    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null) return;

        // Check if resulting string would be valid
        String currentText = getText(0, getLength());
        String resultingText = currentText.substring(0, offset) + str + currentText.substring(offset);

        if (resultingText.length() <= 1 && (str.isEmpty() || (str.matches("[1-9]")))) {
            super.insertString(offset, str, attr);
        }
    }
}