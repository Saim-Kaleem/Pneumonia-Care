package org.deeplearning4j.examples;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class PneumoniaClassifierApplication {

    private static final int height = 150;
    private static final int width = 150;
    private static final int channels = 3; // Colored images (RGB)

    public static double generateData(MultipartFile rawData) throws Exception {
        // Load the model
        File locationToLoad = new File("D:\\deeplearning4j-examples\\dl4j-examples\\src\\main\\resources\\PneumoniaClassifierModel.zip");
        ComputationGraph model = ModelSerializer.restoreComputationGraph(locationToLoad);

        Path tempDir = Files.createTempDirectory("tempFiles");
        File newData = tempDir.resolve(rawData.getOriginalFilename()).toFile();
        Files.copy(rawData.getInputStream(),newData.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // Load and preprocess the new image
        NativeImageLoader loader = new NativeImageLoader(height, width, channels);
        INDArray image = loader.asMatrix(newData);

        // Normalize the image
        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
        scaler.transform(image);

        // Run inference
        INDArray output = model.outputSingle(image);

        // Get the probability for pneumonia (class index 1)
        double pneumoniaProbability = output.getDouble(1);

        // Print the percentage probability for pneumonia
        return pneumoniaProbability * 100;
    }
}