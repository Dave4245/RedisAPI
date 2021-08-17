package b.dave.base;

public interface JedisHandler<T> {

    void handle(T message);

}