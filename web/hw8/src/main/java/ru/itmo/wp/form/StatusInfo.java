package ru.itmo.wp.form;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class StatusInfo {
    @NotNull
    private long id;
    private boolean disabled;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
