package b.dave.base;

import b.dave.base.example.RedisExample;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RedisChannel {

    CUSTOM_CHANNEL(RedisExample.class);

    private final Class<?> classType;

}
