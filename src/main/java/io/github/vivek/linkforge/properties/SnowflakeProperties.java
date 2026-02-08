package io.github.vivek.linkforge.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "snowflake")
@Validated
@Getter
@Setter
public class SnowflakeProperties {

    @NotNull
    private Long epoch;

    @Min(1)
    @Max(20)
    private Long workerIdBits;

    @Min(1)
    @Max(20)
    private Long sequenceBits;

    @Min(0)
    private Integer workerId;
}
