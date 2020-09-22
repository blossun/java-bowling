package bowling.domain.core.state;

import bowling.domain.core.RolledResult;

import static bowling.domain.core.state.ImmutableTwoFallenPins.strikeTwoFallenPins;
import static bowling.domain.core.state.NotAtRolledResult.notAtRolledResult;

final class Strike implements RolledResult {
    static final RolledResult strike = new Strike();

    @Override
    public int getNextRolledResultMergeScore(RolledResult nextRolledResult) {
        final ImmutableTwoFallenPins twoFallenPins = nextRolledResult.twoFallenPins();

        int score = twoFallenPins.firstFallenPinsValue()
            + twoFallenPins.secondFallenPinsValue();

        if (this == nextRolledResult){
            return score + nextRolledResult.getRolledResultScore();
        }

        return score;
    }

    @Override
    public RolledResult nextRolledResult(int fallenPinsValue) {
        return notAtRolledResult().nextRolledResult(fallenPinsValue);
    }

    @Override
    public ImmutableTwoFallenPins twoFallenPins() {
        return strikeTwoFallenPins();
    }

    @Override
    public String description() {
        return "X";
    }
}