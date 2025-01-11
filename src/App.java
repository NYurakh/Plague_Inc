
import controller.MainMenuController;
import view.MainMenuView;

public class App {

    public static void main(String[] args) throws Exception {
        MainMenuView mainMenuView = new MainMenuView();
        new MainMenuController(mainMenuView);
        mainMenuView.setVisible(true);
    }
}
