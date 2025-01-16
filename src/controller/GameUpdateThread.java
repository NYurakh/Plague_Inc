package controller;

class GameUpdateThread extends Thread {
    private final GameController controller;
    private volatile boolean running;
    
    public GameUpdateThread(GameController controller) {
        this.controller = controller;
        this.running = true;
    }
    
    @Override
    public void run() {
        while (running) {
            try {
                if (controller.getGameData().isGameRunning()) {
                    controller.updateGame();
                }
                Thread.sleep(1000); // Update every second
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
