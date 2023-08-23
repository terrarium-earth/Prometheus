package earth.terrarium.prometheus.client.handlers.stuck;

public interface StuckScreen {

    boolean prometheus$isStuck();

    void prometheus$setStuck(boolean stuck);
}
