package b.dave.base.example;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class RedisExample {

    private final UUID uuid;
    private final String name;

}
