package step3.domain;

import step3.domain.dto.PointDTO;
import step3.state.Symbol;
import step3.type.PitchesOrderType;
import step3.type.ResultPitchesType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static step3.type.PitchesOrderType.*;
import static step3.type.ResultPitchesType.createSymbol;

public class BowlingSymbols {
    public static final String ERROR_ALREADY_EXISTS_VALUE = "이미 존재하는 값을 추가할 수 없습니다.";
    public static final String ERROR_NOT_PITCHES = "더 이상 투구할 수 없습니다.";
    public static final int ZERO_SCORE = 0;

    private final Map<PitchesOrderType, Symbol> bowlingSymbols;
    private final int maxPitches;

    public BowlingSymbols(Map<PitchesOrderType, Symbol> bowlingSymbols, int maxPitches) {
        this.bowlingSymbols = bowlingSymbols;
        this.maxPitches = maxPitches;
    }

    public static BowlingSymbols of(int maxPitches) {
        return new BowlingSymbols(new HashMap<>(), maxPitches);
    }

    public BowlingSymbols push(int pitchesCount) {
        Symbol symbol = createSymbol(getPointDTO(pitchesCount));
        PitchesOrderType type = nextType(size());

        return push(type, symbol);
    }

    private BowlingSymbols push(PitchesOrderType orderType, Symbol symbol) {
        isAllowType(orderType);
        isValid();
        bowlingSymbols.put(orderType, symbol);

        return this;
    }

    private void isValid() {
        if (isMaximumSize()) {
            throw new IllegalArgumentException(ERROR_NOT_PITCHES);
        }
    }

    private boolean isMaximumSize() {
        return size() == maxPitches;
    }

    private void isAllowType(PitchesOrderType type) {
        if (Objects.nonNull(bowlingSymbols.get(type))) {
            throw new IllegalArgumentException(ERROR_ALREADY_EXISTS_VALUE);
        }
    }

    public String getSymbol() {
        return Stream.of(FIRST,SECOND,THIRD)
                .filter(type->{
                    Symbol symbol = get(type);
                    return !Objects.equals(symbol, Symbol.defaultSymbol());
                })
                .map(type->get(type).getSymbol())
                .collect(Collectors.joining("|"));
    }

    private Symbol get(PitchesOrderType type) {
        return Optional.ofNullable(bowlingSymbols.get(type))
                .orElse(Symbol.defaultSymbol());
    }

    public int getScore(PitchesOrderType type) {
        return get(type).getPoint();
    }

    public int size() {
        return bowlingSymbols.size();
    }

    public ResultPitchesType getType() {
        return ResultPitchesType.getType(getPointDTO());
    }

    public int getScore() {
        return bowlingSymbols.values()
                .stream()
                .map(Symbol::getPoint)
                .reduce(Integer::sum)
                .orElse(ZERO_SCORE);
    }

    public int getScore(PitchesOrderType... types) {
        List<PitchesOrderType> pitchesOrderTypes = asList(types);

        return bowlingSymbols.entrySet()
                .stream()
                .filter(entry -> pitchesOrderTypes.contains(entry.getKey()))
                .map(entry -> entry.getValue().getPoint())
                .reduce(Integer::sum)
                .orElse(ZERO_SCORE);
    }

    public PointDTO getPointDTO(int pitchesCount) {
        PointDTO.Builder builder = createPointDTOBuilder();
        return builder.fill(pitchesCount).build();
    }

    public PointDTO getPointDTO() {
        PointDTO.Builder builder = createPointDTOBuilder();
        return builder.build();
    }

    private PointDTO.Builder createPointDTOBuilder() {
        int currentSize = size();
        PointDTO.Builder builder = PointDTO.Builder(currentSize, maxPitches);

        if (currentSize >= 1) {
            builder = builder.first(getScore(FIRST));
        }
        if (currentSize >= 2) {
            builder = builder.second(getScore(SECOND));
        }
        if (currentSize == 3) {
            builder = builder.third(getScore(THIRD));
        }
        return builder;
    }

    public ResultPitchesType getType(PitchesOrderType type) {
        return get(type).getType();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BowlingSymbols)) return false;
        BowlingSymbols that = (BowlingSymbols) o;
        return maxPitches == that.maxPitches && Objects.equals(bowlingSymbols, that.bowlingSymbols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bowlingSymbols, maxPitches);
    }

    public boolean existsSymbol(PitchesOrderType orderType) {
        Symbol symbol = get(orderType);
        return !symbol.equals(Symbol.defaultSymbol());
    }
}