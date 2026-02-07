package com.blocknights.data;

import java.util.ArrayList;
import java.util.List;

public class WaveDefinition {
    private final int id;
    private final List<WaveGroup> groups = new ArrayList<>();
    private int delayBeforeNext = 10; // Secondes de pause après cette vague

    public WaveDefinition(int id) {
        this.id = id;
    }

    public void addGroup(WaveGroup group) {
        groups.add(group);
    }

    public List<WaveGroup> getGroups() { return groups; }
    public int getId() { return id; }
    
    public int getDelayBeforeNext() { return delayBeforeNext; }
    public void setDelayBeforeNext(int delay) { this.delayBeforeNext = delay; }
}