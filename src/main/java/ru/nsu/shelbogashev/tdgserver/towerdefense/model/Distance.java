package ru.nsu.shelbogashev.tdgserver.towerdefense.model;

public class Distance {
    private final Metric metric;
    private final long value;

    public Distance(long value, Metric metric) {
        this.value = value;
        this.metric = metric;
    }

    /* Common is a value with bounds: [0, 4294967296]. */
    private static long convertTo(long value, Metric metricBefore, Metric metricAfter) {
        if (metricAfter.equals(metricBefore)) return value;

        long common = switch (metricBefore) {
            case PERCENT -> (long) 42949672.96d * value;
        };

        return switch (metricAfter) {
            case PERCENT -> (long) ((double) value / 4294967296f);
        };
    }

    long getAs(Metric metric) {
        return convertTo(value, this.metric, metric);
    }

    public enum Metric {
        PERCENT
    }
}