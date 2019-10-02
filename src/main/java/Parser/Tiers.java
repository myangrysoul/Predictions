package Parser;

public enum Tiers {
    MAJOR(1),
    BIGLAN(2),
    OTHER(3);
    private final int tier;

    Tiers(int tier) {
        this.tier = tier;
    }

    public int getTier() {

        return tier;
    }
}
