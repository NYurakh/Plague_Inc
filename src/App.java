
import controller.MainMenuController;
import javax.swing.UIManager;
import view.MainMenuView;

public class App {

    public static void main(String[] args) throws Exception {

        // to look like system style
        try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
       System.out.println("Something was wrong!");
    }
        MainMenuView mainMenuView = new MainMenuView();
        new MainMenuController(mainMenuView);
        mainMenuView.setVisible(true);
    }
}
