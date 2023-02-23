package id.rajaopak.xquest.api.player;

import java.util.List;

public interface PlayerManager {

    public List<String> getAvailableQuest();

    public List<String> getInProgressQuest();

    public List<String> getFinishedQuest();

}
