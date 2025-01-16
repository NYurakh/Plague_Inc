package controller;
class UIUpdateThread extends Thread{
    private final GameController controller;
    private volatile boolean running;
    
    public UIUpdateThread(GameController controller) {
        this.controller = controller;
        this.running = true;
    }
    
    @Override
    public void run() {
        while (running) {
            try {
                if (controller.getGameData().isGameRunning()) {
                    // Update time label
                    long elapsed = (System.currentTimeMillis() - controller.getGameData().getStartTimeMillis()) / 1000;
                    controller.getGameView().updateTimerLabel(elapsed);
                    controller.getGameView().updateScoreLabel(controller.getGameData().getPoints());
                    
                    // Repaint the map for animations
                    controller.getGameView().getMapPanel().repaint();
                }
                Thread.sleep(50); // refresh rate
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    public void stopThread() {
        running = false;
    }
}
