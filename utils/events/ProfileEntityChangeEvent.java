package com.example.guiex1.utils.events;

import com.example.guiex1.domain.Message;
import com.example.guiex1.domain.Profile;

public class ProfileEntityChangeEvent implements Event{
    private ChangeEventType type;
    private Profile data, oldData;

    public ProfileEntityChangeEvent(ChangeEventType type, Profile data) {
        this.type = type;
        this.data = data;
    }
    public ProfileEntityChangeEvent(ChangeEventType type, Profile data, Profile oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Profile getData() {
        return data;
    }

    public Profile getOldData() {
        return oldData;
    }
}
