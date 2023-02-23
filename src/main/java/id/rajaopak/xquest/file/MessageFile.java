package id.rajaopak.xquest.file;

import id.rajaopak.xquest.XQuest;

public class MessageFile extends FileBuilder{

    public MessageFile(String configName) {
        super(XQuest.getInstance(), configName);
    }
}
