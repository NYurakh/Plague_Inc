package view;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

public class MainMenuView extends JFrame{
    private JButton newGameButton;
    private JButton highScoresButton;
    private JButton exitButton;


    public MainMenuView(){
        setTitle("Antiplague Coronavirus - Main Menu");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3,1,10,10));

        newGameButton = new JButton("New Game");
        highScoresButton = new JButton("High Scores");
        exitButton = new JButton("Exit");


        add(newGameButton);
        add(highScoresButton);
        add(exitButton);
    }

    public JButton getNewGameButton(){
        return newGameButton;
    }

    public JButton getHighScoresButton() {
        return highScoresButton;
    }

    public JButton getExitButton() {
        return exitButton;
    }
}
