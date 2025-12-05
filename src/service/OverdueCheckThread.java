package service;

import model.Request;
import java.time.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;

public class OverdueCheckThread extends Thread {
    private final RequestService requestService;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final boolean guiMode;
    private final java.util.function.Consumer<List<Request>> callback;

    // callback is invoked when overdue found (for GUI notifications)
    public OverdueCheckThread(RequestService rs, boolean guiMode, java.util.function.Consumer<List<Request>> callback) {
        this.requestService = rs;
        this.guiMode = guiMode;
        this.callback = callback;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                List<Request> overdue = requestService.overdueRequests();
                if (!overdue.isEmpty()) {
                    if (guiMode && callback != null) {
                        // ensure Swing thread safety
                        SwingUtilities.invokeLater(() -> callback.accept(overdue));
                    } else if (callback != null) {
                        callback.accept(overdue);
                    }
                }
                Thread.sleep(15_000); // check every 15 sec
            } catch (InterruptedException e) {
                running.set(false);
            } catch (Exception ex){ ex.printStackTrace(); }
        }
    }

    public void stopRunning() { running.set(false); this.interrupt(); }
}
