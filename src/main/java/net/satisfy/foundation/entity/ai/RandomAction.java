package net.satisfy.foundation.entity.ai;

public interface RandomAction {
    float chance();

    int duration();

    boolean isPossible();

    boolean isInterruptable();

    void onStart();

    default void onTick(int tick) {
    }

    void onStop();

    default boolean canMove() {
        return false;
    }
}
