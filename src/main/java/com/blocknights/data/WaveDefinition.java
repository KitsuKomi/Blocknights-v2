package com.blocknights.data;

import java.util.ArrayList;
import java.util.List;

public class WaveDefinition {
    private final int id;
    private final List<WaveGroup> groups = new ArrayList<>();
    private int delayBeforeNext = 10;

    public WaveDefinition(int id) {
        this.id = id;
    }

    public int getId() { return id; }

    public List<WaveGroup> getGroups() { return groups; }
    
    public void addGroup(WaveGroup group) { groups.add(group); }

    public int getDelayBeforeNext() { return delayBeforeNext; }
    public void setDelayBeforeNext(int delayBeforeNext) { this.delayBeforeNext = delayBeforeNext; }
}