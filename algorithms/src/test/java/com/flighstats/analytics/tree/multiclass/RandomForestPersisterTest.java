package com.flighstats.analytics.tree.multiclass;

import com.flighstats.analytics.tree.Item;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static com.flighstats.analytics.tree.multiclass.RandomForestTrainerTest.Humidity.HIGH;
import static com.flighstats.analytics.tree.multiclass.RandomForestTrainerTest.Humidity.NORMAL;
import static com.flighstats.analytics.tree.multiclass.RandomForestTrainerTest.Outlook.RAIN;
import static com.flighstats.analytics.tree.multiclass.RandomForestTrainerTest.Temp.HOT;
import static com.flighstats.analytics.tree.multiclass.RandomForestTrainerTest.Temp.MILD;
import static com.flighstats.analytics.tree.multiclass.RandomForestTrainerTest.Wind.WEAK;
import static com.flighstats.analytics.tree.multiclass.RandomForestTrainerTest.*;
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

        assertEquals((Integer) 1, loaded.evaluate(new Item("a", tennisData(RAIN, HOT, NORMAL, WEAK))));
        assertEquals((Integer) 1, loaded.evaluate(new Item("b", tennisData(RAIN, MILD, HIGH, WEAK))));

        assertEquals(forest, loaded);
    }

    private TrainingResults train() {
        List<LabeledItem> labeledItems = buildTennisTrainingSet();
        RandomForestTrainer trainer = new RandomForestTrainer(new DecisionTreeTrainer(new EntropyCalculator()));
        return trainer.train("test", 50, labeledItems, tennisAttributes(), -1);
    }

}