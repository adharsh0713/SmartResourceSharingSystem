package service;

import java.util.concurrent.atomic.AtomicBoolean;

public class AutoSaveThread extends Thread {
    private final UserService userService;
    private final ResourceService resourceService;
    private final RequestService requestService;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public AutoSaveThread(UserService us, ResourceService rs, RequestService rqs) {
        this.userService = us;
        this.resourceService = rs;
        this.requestService = rqs;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                // All services call DataStore internally on changes; but here we force save snapshot
                userService.internalMap(); // reading map (already persisted by each operation)
                resourceService.internalMap();
                requestService.internalMap();
                // sleep 30 seconds
                Thread.sleep(30_000);
            } catch (InterruptedException e) {
                running.set(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void stopRunning() { running.set(false); this.interrupt(); }
}
