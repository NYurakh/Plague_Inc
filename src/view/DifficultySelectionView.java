package view;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class DifficultySelectionView extends JDialog {
    private JComboBox<String> difficultyComboBox;
    private JButton confirmButton;
    private String selectedDifficulty;  
    
    public DifficultySelectionView(JFrame parent) {
        super(parent, "Select Difficulty", true);
        setSize(300, 120);
        setLocationRelativeTo(parent);
        setLayout(new FlowLayout());

        difficultyComboBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        confirmButton = new JButton("OK");

        add(new JLabel("Choose Difficulty:"));
        add(difficultyComboBox);
        add(confirmButton);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedDifficulty = (String) difficultyComboBox.getSelectedItem();
                dispose();
            }
        });
    }

    public String getSelectedDifficulty() {
        return selectedDifficulty;
    }
    
}