package com.example.demo.akka.actors;

import akka.actor.UntypedActor;
import akka.actor.ActorRef;
import java.util.Arrays;

public class MapperActor {
	

	public class Mapper extends UntypedActor {
	    private final ActorRef[] reducers;

	    public Mapper(ActorRef[] reducers) {
	        this.reducers = reducers;
	    }

	    @Override
	    public void onReceive(Object message) throws Throwable {
	        if (message instanceof String) {
	            String line = (String) message;
	            Arrays.stream(line.split("\s+")).forEach(word -> {
	                ActorRef reducer = reducers[Math.abs(word.hashCode()) % reducers.length];
	                reducer.tell(word, getSelf());
	            });
	        } else {
	            unhandled(message);
	        }
	    }
	}
}
