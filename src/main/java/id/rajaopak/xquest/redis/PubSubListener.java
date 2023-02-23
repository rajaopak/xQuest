package id.rajaopak.xquest.redis;

import id.rajaopak.xquest.XQuest;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.JedisPubSub;

public class PubSubListener extends JedisPubSub {

    private final XQuest plugin;

    public PubSubListener(XQuest plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onMessage(String channel, String message) {
        JSONObject object;

        try {
            object = new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
