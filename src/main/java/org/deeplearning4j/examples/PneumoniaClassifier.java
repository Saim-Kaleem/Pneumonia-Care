package org.deeplearning4j.examples;

import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.variational.BernoulliReconstructionDistribution;
import org.deeplearning4j.nn.conf.layers.variational.VariationalAutoencoder;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.util.Random;

public class PneumoniaClassifier {

    private static final int height = 150;
    private static final int width = 150;
    private static final int channels = 3; // Colored images (RGB)
    private static final int numClasses = 2; // Pneumonia or No Pneumonia
    private static final int batchSize = 64;
    private static final int numEpochs = 10;

    public static void main(String[] args) throws Exception {

        // Load and preprocess your colored X-ray image dataset using DataSetIterators
        // Define data directory
        File trainDataDir = new File("D:\\deeplearning4j-examples\\dl4j-examples\\src\\main\\resources\\chest_xray\\train");
        File testDataDir = new File("D:\\deeplearning4j-examples\\dl4j-examples\\src\\main\\resources\\chest_xray\\test");

        // Define random seed for shuffling and splitting
        Random randNumGen = new Random(1234);

        // Define input split for train and test datasets
        FileSplit train = new FileSplit(trainDataDir, NativeImageLoader.ALLOWED_FORMATS, randNumGen);
        FileSplit test = new FileSplit(testDataDir, NativeImageLoader.ALLOWED_FORMATS, randNumGen);

        // Extract the parent path as the image label
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();

        // Define image record reader for train and test datasets
        ImageRecordReader trainRecordReader = new ImageRecordReader(height, width, channels, labelMaker);
        trainRecordReader.initialize(train);

        ImageRecordReader testRecordReader = new ImageRecordReader(height, width, channels, labelMaker);
        testRecordReader.initialize(test);

        // Define image data normalization
        DataNormalization normalization = new ImagePreProcessingScaler(0, 1);

        // Create DataSetIterators for train and test datasets with numClasses
        DataSetIterator trainIterator = new RecordReaderDataSetIterator(trainRecordReader, batchSize, 1, numClasses);
        DataSetIterator testIterator = new RecordReaderDataSetIterator(testRecordReader, batchSize, 1, numClasses);

        // Apply normalization to the iterators
        trainIterator.setPreProcessor(normalization);
        testIterator.setPreProcessor(normalization);

        // Define CNN architecture with Variational Autoencoder
        ComputationGraphConfiguration config = new NeuralNetConfiguration.Builder()
                .seed(1234)
                .updater(new RmsProp(1e-3))
                .weightInit(WeightInit.XAVIER)
                .l2(1e-4)
                .graphBuilder()
                .addInputs("input")
                .setInputTypes(InputType.convolutional(height, width, channels))
                .layer("vae", new VariationalAutoencoder.Builder()
                        .activation(Activation.LEAKYRELU)
                        .encoderLayerSizes(20, 10) // Encoder layers with 20 and 10 nodes respectively
                        .decoderLayerSizes(10, 20) // Decoder layers with 10 and 20 nodes respectively
                        .pzxActivationFunction(Activation.IDENTITY) // p(z|data) activation function
                        .reconstructionDistribution(new BernoulliReconstructionDistribution(Activation.SIGMOID.getActivationFunction())) // Bernoulli distribution for p(data|z) (binary or 0 to 1 data only)
                        .nIn(height * width * channels) // Number of input nodes
                        .nOut(2) // Size of the latent variable space: p(z|x). 2 dimensions here for plotting, use more in general
                        .build(), "input")
                .addLayer("dense", new DenseLayer.Builder()
                        .activation(Activation.RELU)
                        .nOut(100)
                        .build(), "vae")
                .addLayer("output", new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nOut(numClasses)
                        .build(), "dense")
                .setOutputs("output")
                .build();

        ComputationGraph model = new ComputationGraph(config);
        model.init();

        // Add listeners to the model
        model.setListeners(new ScoreIterationListener(10));

        // Train the model
        for (int i = 0; i < numEpochs; i++) {
            model.fit(trainIterator);
            System.out.println("Completed epoch " + i);
        }

        // Save the model
        File locationToSave = new File("D:\\deeplearning4j-examples\\dl4j-examples\\src\\main\\resources\\PneumoniaClassifierModel.zip"); // where to save the model
        boolean saveUpdater = true; // whether to save the updater state as well
        ModelSerializer.writeModel(model, locationToSave, saveUpdater);

        // Evaluate the model
        Evaluation evaluation = model.evaluate(testIterator);
        System.out.println("Evaluation metrics: " + evaluation.stats());
    }
}