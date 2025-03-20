package com.example.demo.actor;

import akka.actor.UntypedActor;
import akka.actor.ActorRef;
import java.util.Arrays;

public class MapperActor extends UntypedActor {
    private final ActorRef[] reducers;

    // Constructeur prenant un tableau d'ActorRef
    public MapperActor(ActorRef[] reducers) {
        this.reducers = reducers;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof String) {
            String line = (String) message;
            Arrays.stream(line.split("\\s+")).forEach(word -> {
                if (!word.isEmpty()) {
                    ActorRef reducer = reducers[Math.abs(word.hashCode()) % reducers.length];
                    reducer.tell(word, getSelf());
                }
            });
        } else {
            unhandled(message);
        }
    }
}