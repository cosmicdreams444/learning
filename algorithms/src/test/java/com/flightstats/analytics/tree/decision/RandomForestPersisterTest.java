package com.flightstats.analytics.tree.decision;

import com.flightstats.analytics.tree.LabeledMixedItem;
import com.flightstats.analytics.tree.MixedItem;
import com.flightstats.analytics.tree.Splitter;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

import static com.flightstats.analytics.tree.decision.RandomForestTrainerTest.Humidity.HIGH;
import static com.flightstats.analytics.tree.decision.RandomForestTrainerTest.Humidity.NORMAL;
import static com.flightstats.analytics.tree.decision.RandomForestTrainerTest.Outlook.RAIN;
import static com.flightstats.analytics.tree.decision.RandomForestTrainerTest.Temp.HOT;
import static com.flightstats.analytics.tree.decision.RandomForestTrainerTest.Temp.MILD;
import static com.flightstats.analytics.tree.decision.RandomForestTrainerTest.Wind.WEAK;
import static com.flightstats.analytics.tree.decision.RandomForestTrainerTest.*;
import static org.junit.Assert.assertEquals;

public class RandomForestPersisterTest {

    @Test
    public void testSaveAndLoad() throws Exception {
        ByteArrayOutputStream writer = new ByteArrayOutputStream();

        TrainingResults trainingResults = train();
        RandomForest forest = trainingResults.getForest();

        RandomForestPersister testClass = new RandomForestPersister();
        testClass.save(forest, writer);
        writer.flush();

        RandomForest loaded = testClass.load(new ByteArrayInputStream(writer.toByteArray()));

        assertEquals((Integer) 1, loaded.evaluate(new MixedItem("a", tennisData(RAIN, HOT, NORMAL, WEAK), new HashMap<>())));
        assertEquals((Integer) 1, loaded.evaluate(new MixedItem("b", tennisData(RAIN, MILD, HIGH, WEAK), new HashMap<>())));

        assertEquals(forest, loaded);
    }

    private TrainingResults train() {
        List<LabeledMixedItem<Integer>> labeledItems = buildTennisTrainingSet();
        RandomForestTrainer trainer = new RandomForestTrainer(new DecisionTreeTrainer(new Splitter<>()));
        return trainer.train("test", 50, labeledItems, tennisAttributes(), -1);
    }

}