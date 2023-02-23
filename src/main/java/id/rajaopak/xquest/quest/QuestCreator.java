package id.rajaopak.xquest.quest;

import id.rajaopak.xquest.XQuest;
import id.rajaopak.xquest.util.Utils;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class QuestCreator {

    private int id;
    private String name;
    private String description;
    private String startMessage;
    private String endMessage;
    private Integer npcId;
    private Long restartTime;
    private final List<ObjectBuilder> object = new ArrayList<>();
    private final HashMap<Integer, String> dialog = new HashMap<>();
    private final HashMap<Object, Object> startRewards = new HashMap<>();
    private final HashMap<Object, Object> endRewards = new HashMap<>();

    public QuestCreator() {
        try (Stream<Path> files = Files.walk(XQuest.getInstance().getSaveFolder().toPath(), Integer.MAX_VALUE, FileVisitOption.FOLLOW_LINKS)) {
            this.id = files.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith("yml"))
                    .map(path -> path.toString().replace(".yml", ""))
                    .mapToInt(Integer::parseInt).max().orElse(0);
        } catch (IOException e) {
            Utils.logSevere("An error occurred while reading all existed id.", e);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ObjectBuilder> getObject() {
        return object;
    }

    public void addObject(ObjectBuilder builder) {
        this.object.add(builder);
    }

    public HashMap<Integer, String> getDialog() {
        return dialog;
    }

    public void setDialog(Integer id, String message) {
        this.dialog.put(id, message);
    }

    public HashMap<Object, Object> getStartRewards() {
        return this.startRewards;
    }

    public void setStartRewards(Object object, Object object1) {
        this.startRewards.put(object, object1);
    }

    public HashMap<Object, Object> getEndRewards() {
        return this.endRewards;
    }

    public void setEndRewards(Object object, Object object1) {
        this.endRewards.put(object, object1);
    }
    public ObjectBuilder objectBuilder() {
        return new ObjectBuilder();
    }

    public boolean build() {
        //TODO: build structure
        return false;
    }

    public Long getRestartTime() {
        return restartTime;
    }

    public void setRestartTime(Long restartTime) {
        this.restartTime = restartTime;
    }

    public String getStartMessage() {
        return startMessage;
    }

    public void setStartMessage(String startMessage) {
        this.startMessage = startMessage;
    }

    public String getEndMessage() {
        return endMessage;
    }

    public void setEndMessage(String endMessage) {
        this.endMessage = endMessage;
    }

    public Integer getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    public static class ObjectBuilder {
        private QuestType type;
        private Object objectOne;
        private Object objectTwo;

        public QuestType getType() {
            return type;
        }

        public ObjectBuilder setType(QuestType type) {
            this.type = type;
            return this;
        }

        public Object getObjectOne() {
            return objectOne;
        }

        public ObjectBuilder setObjectOne(Object objectOne) {
            this.objectOne = objectOne;
            return this;
        }

        public Object getObjectTwo() {
            return objectTwo;
        }

        public ObjectBuilder setObjectTwo(Object objectTwo) {
            this.objectTwo = objectTwo;
            return this;
        }
    }
}
