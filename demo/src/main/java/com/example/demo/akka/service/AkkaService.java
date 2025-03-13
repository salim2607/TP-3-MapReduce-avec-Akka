package com.example.demo.akka.service;

// Imports Akka
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import jakarta.annotation.PostConstruct;

import static akka.pattern.Patterns.ask;

// Imports pour la configuration Akka
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

// Imports Spring
import org.springframework.stereotype.Service;

// Imports Java
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

// Imports des acteurs personnalisés
import com.example.demo.akka.actors.MapperActor;
import com.example.demo.akka.actors.ReducerActor;
import com.example.demo.akka.actors.*;

import com.example.demo.akka.messages.*;


@Service
public class AkkaService {

    private ActorSystem mapperSystem;
    private ActorSystem reducerSystem;
    private final List<ActorRef> mappers = new ArrayList<>();
    private final List<ActorRef> reducers = new ArrayList<>();
    private static final int NUM_MAPPERS = 3;
    private static final int NUM_REDUCERS = 2;

    @PostConstruct
    public void init() {
        initActorSystems();
    }

    private void initActorSystems() {
        Config mapperConfig = ConfigFactory.load("application-mapper.conf");
        Config reducerConfig = ConfigFactory.load("application-reducer.conf");

        mapperSystem = ActorSystem.create("MapperSystem", mapperConfig);
        reducerSystem = ActorSystem.create("ReducerSystem", reducerConfig);

        createReducers();
        createMappers();
    }

    private void createReducers() {
        for (int i = 0; i < NUM_REDUCERS; i++) {
            ActorRef reducer = reducerSystem.actorOf(Props.create(ReducerActor.class), "reducer-" + i);
            reducers.add(reducer);
        }
    }

    private void createMappers() {
        for (int i = 0; i < NUM_MAPPERS; i++) {
            ActorRef mapper = mapperSystem.actorOf(
                Props.create(MapperActor.class, reducers.toArray(new ActorRef[0])),
                "mapper-" + i
            );
            mappers.add(mapper);
        }
    }

    public void processLine(String line, int lineNumber) {
        if (!mappers.isEmpty()) {
            ActorRef mapper = mappers.get(lineNumber % NUM_MAPPERS);
            mapper.tell(new ProcessLine(line), ActorRef.noSender());
        }
    }

//    public int getWordCount(String word) {
//        try {
//            ActorRef reducer = selectReducer(word);
//            return (int) ask(reducer, new GetWordCount(word), Duration.ofSeconds(3))
//                .toCompletableFuture()
//                .get();
//        } catch (Exception e) {
//            throw new RuntimeException("Erreur lors de la récupération du compte pour : " + word, e);
//        }
//    }

    private ActorRef selectReducer(String word) {
        int index = Math.abs(word.hashCode()) % NUM_REDUCERS;
        return reducers.get(index);
    }

	public int getWordCount(String word) {
		// TODO Auto-generated method stub
		return 0;
	}

//    @PreDestroy
//    public void shutdown() {
//        if (mapperSystem != null) {
//            mapperSystem.terminate();
//        }
//        if (reducerSystem != null) {
//            reducerSystem.terminate();
//        }
//    }
}