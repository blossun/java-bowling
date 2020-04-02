package bowling.domain.frame;

import bowling.domain.state.Spare;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;

class FramesTest {

    @Test
    @DisplayName("최근 프레임 상태 확인")
    void checkLastFrameStatus() {
        LinkedList<Frame> framesData = new LinkedList<>();
        framesData.add(new Frame(1));
        Frames frames = new Frames(framesData);

        frames.bowl(2);
        frames.bowl(2);
        frames.bowl(3);
        frames.bowl(3);
        frames.bowl(10);
        frames.bowl(4);
        frames.bowl(6);

        Frame frame = frames.getFrames().getLast();

        assertThat(frame.getState()).isInstanceOf(Spare.class);
        assertThat(frame.getFrameNumber()).isEqualTo(4);
        assertThat(frames.getFrames().size()).isEqualTo(4);
    }

    @Test
    @DisplayName("마지막 프레임 확인")
    void checkFinalFrameStatus() {
        LinkedList<Frame> framesData = new LinkedList<>();
        framesData.add(new Frame(1));
        Frames frames = new Frames(framesData);
        frames.bowl(10);
        frames.bowl(10);
        frames.bowl(10);
        frames.bowl(10);
        frames.bowl(10);
        frames.bowl(10);
        frames.bowl(10);
        frames.bowl(10);
        frames.bowl(10);
        // 마지막 프레임
        frames.bowl(3);
        frames.bowl(7);
        Frame finalFrame = frames.getFrames().getLast();
        assertThat(finalFrame.getState()).isInstanceOf(Spare.class);
        assertThat(finalFrame.isFinish()).isTrue();
    }

    @Test
    @DisplayName("프레임 점수 계산")
    void calculateByFrame() {
        LinkedList<Frame> framesData = new LinkedList<>();
        framesData.add(new Frame(1));
        Frames frames = new Frames(framesData);
        frames.bowl(3);
        frames.bowl(6);

        frames.bowl(5);
        frames.bowl(5);

        frames.bowl(2);
        frames.bowl(8);

        frames.bowl(10);

        frames.bowl(10);

        frames.bowl(8);
        frames.bowl(1);

        assertThat(frames.getFramesByCalculationScore().size()).isEqualTo(6);
    }
}