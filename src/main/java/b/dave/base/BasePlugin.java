package b.dave.base;

import b.dave.base.example.RedisExample;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class BasePlugin extends JavaPlugin implements Listener {

    private RedisAPI redisAPI;

    @Override
    public void onEnable() {

        redisAPI = new RedisAPI();

        redisAPI.subscribe(RedisChannel.CUSTOM_CHANNEL, data -> getServer().getScheduler().runTask(this, () -> {

            String playerName = data.getName();
            UUID playerUUID = data.getUuid();

            // Do whatever you want :)

        }), RedisExample.class);

        getServer().getScheduler().runTaskAsynchronously(this, () -> {

            // Setting the value Test = This works
            redisAPI.runTransaction(jedis -> jedis.set("Test", "This works"));

            // Gets the value Test
            String message = redisAPI.runReturnTransaction(jedis -> jedis.get("Test"));

            // Broadcasts the value Test
            getServer().getScheduler().runTask(this, () -> getServer().broadcastMessage(message));

        });

        getServer().getPluginManager().registerEvents(this, this);

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        RedisExample redisExample = new RedisExample(event.getPlayer().getUniqueId(), event.getPlayer().getName());

        redisAPI.publish(RedisChannel.CUSTOM_CHANNEL, redisExample);

    }

}
