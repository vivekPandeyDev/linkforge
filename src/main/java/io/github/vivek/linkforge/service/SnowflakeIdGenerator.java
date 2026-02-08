package io.github.vivek.linkforge.service;

import io.github.vivek.linkforge.properties.SnowflakeProperties;
import org.springframework.stereotype.Service;

@Service
public class SnowflakeIdGenerator {

    private final long epoch;
    private final long maxSequence;
    private final long workerIdShift;
    private final long timestampShift;
    private final long workerId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeIdGenerator(SnowflakeProperties properties) {
        this.epoch = properties.getEpoch();
        Long workerIdBits = properties.getWorkerIdBits();
        Long sequenceBits = properties.getSequenceBits();
        this.workerId = properties.getWorkerId();

        long maxWorkerId = ~(-1L << workerIdBits);
        this.maxSequence = ~(-1L << sequenceBits);

        if (workerId < 0 || workerId > maxWorkerId) {
            throw new IllegalArgumentException("workerId must be between 0 and " + maxWorkerId);
        }

        this.workerIdShift = sequenceBits;
        this.timestampShift = workerIdBits + sequenceBits;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        return ((timestamp - epoch) << timestampShift) | (workerId << workerIdShift) | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            Thread.onSpinWait();   // Java 9+
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
