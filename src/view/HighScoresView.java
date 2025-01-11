package view;

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.*;
import model.HighScoreRecord;

public class HighScoresView extends JFrame {
    private JList<String> highScoresList;

    public HighScoresView() {
        setTitle("High Scores");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        highScoresList = new JList<>();
        add(new JScrollPane(highScoresList), BorderLayout.CENTER);
    }

    public void setHighScores(List<HighScoreRecord> scores) {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (HighScoreRecord rec : scores) {
            model.addElement(rec.toString());
        }
        highScoresList.setModel(model);
    }
}
