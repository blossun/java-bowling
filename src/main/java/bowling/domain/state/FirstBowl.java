package bowling.domain.state;

import bowling.domain.Pins;
import bowling.domain.exception.InvalidPitchException;

public class FirstBowl implements State {

    private static final String GUTTER = "-";

    private final Pins firstPins;

    public FirstBowl(Pins firstPins) {
        this.firstPins = firstPins;
    }

    public static State create(Pins firstPins) {
        return new FirstBowl(firstPins);
    }

    @Override
    public State pitch(Pins secondPins) {
        if(firstPins.exceedAllPins(secondPins)) {
            throw new InvalidPitchException(secondPins);
        }

        if (firstPins.isSpare(secondPins)) {
            return Spare.create(firstPins);
        }

        return Miss.of(firstPins, secondPins);
    }

    @Override
    public boolean isFrameEnd() {
        return false;
    }

    @Override
    public String getSymbol() {
        return firstPins.isGutter() ? GUTTER : String.valueOf(firstPins);
    }

}