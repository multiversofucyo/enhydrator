package com.airhacks.enhydrator.flexpipe;

import com.airhacks.enhydrator.in.JDBCSource;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author airhacks.com
 */
public class Plumber {

    private final JAXBContext context;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    private final String baseFolder;
    private final String configurationFolder;

    public Plumber(String baseFolder, String configurationFolder) {
        this.baseFolder = baseFolder;
        this.configurationFolder = configurationFolder;
        try {
            Files.createDirectories(Paths.get(baseFolder, configurationFolder));
            this.context = JAXBContext.newInstance(JDBCSource.class, JDBCPipeline.class);
            this.marshaller = context.createMarshaller();
            this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            this.unmarshaller = context.createUnmarshaller();
        } catch (JAXBException | IOException ex) {
            throw new IllegalStateException("Plumber construction failed ", ex);
        }
    }

    public Pipeline fromConfiguration(String pipeName) {
        Path path = getPath(pipeName);
        BufferedReader bufferedReader;
        try {
            bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
            return (Pipeline) unmarshaller.unmarshal(bufferedReader);
        } catch (IOException | JAXBException ex) {
            throw new IllegalStateException("Cannot deserialize pipeline with name: " + pipeName, ex);
        }
    }

    Path getPath(String pipeName) {
        Objects.requireNonNull(pipeName, "Path has to be set");
        return Paths.get(baseFolder, configurationFolder, pipeName + ".xml");
    }

    public void intoConfiguration(Pipeline pipeline) {
        Objects.requireNonNull(pipeline, "Cannot serialize a null pipeline");
        String name = pipeline.getName();
        Path path = getPath(name);
        BufferedWriter writer;
        try {
            writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
            marshaller.marshal(pipeline, writer);
        } catch (IOException | JAXBException ex) {
            throw new IllegalStateException("Cannot create configuration for pipeline ", ex);
        }

    }
}