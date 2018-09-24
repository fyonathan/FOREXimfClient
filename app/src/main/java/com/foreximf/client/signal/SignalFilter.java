package com.foreximf.client.signal;

import java.util.List;

/**
 * A model class for spinner filter in {@link SignalFragment}.
 * Called and declared in {@link SignalViewModel}
 */
public class SignalFilter {
    private List<Integer> status;
    private List<Integer> pair;
    private List<Integer> group;

    SignalFilter(List<Integer> status, List<Integer> pair, List<Integer> group) {
        this.status = status;
        this.pair = pair;
        this.group = group;
    }

    public List<Integer> getStatus() {
        return status;
    }

    public List<Integer> getPair() {
        return pair;
    }

    public List<Integer> getGroup() {
        return group;
    }
}
