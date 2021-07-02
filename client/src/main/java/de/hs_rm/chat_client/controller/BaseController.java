package de.hs_rm.chat_client.controller;

import de.hs_rm.chat_client.Environment;

public abstract class BaseController {
    private Environment environment;

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public abstract String getViewPath();

    public Environment getEnvironment() {
        return environment;
    }

    public void navigateTo(BaseController controller) {
        environment.navigateTo(controller);
    }

    public void stopApp() { environment.stopApp(); }
}
