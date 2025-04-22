package com.mylearn.agentgate.exception;

public class AgentException extends RuntimeException{
    public AgentException(String msg) {
        super(msg);
    }

    public AgentException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
