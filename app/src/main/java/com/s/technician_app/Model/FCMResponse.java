package com.s.technician_app.Model;

import java.util.List;

public class FCMResponse {
    private long multicast_id, message_id;
    private int success, failure, cannonical_ids;
    private List<FCMResult> results;

    public FCMResponse() {
    }

    public long getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(long multicast_id) {
        this.multicast_id = multicast_id;
    }

    public long getMessage_id() {
        return message_id;
    }

    public void setMessage_id(long message_id) {
        this.message_id = message_id;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getCannonical_ids() {
        return cannonical_ids;
    }

    public void setCannonical_ids(int cannonical_ids) {
        this.cannonical_ids = cannonical_ids;
    }

    public List<FCMResult> getResults() {
        return results;
    }

    public void setResults(List<FCMResult> results) {
        this.results = results;
    }
}
