package pro.jness.pdf.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.jness.pdf.dto.SourcesData;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * @author Aleksandr Streltsov (jness.pro@gmail.com)
 * on 26/08/16
 */
public class PdfGenerationTask implements Callable<PdfGenerationResult> {

    private static final Logger logger = LoggerFactory.getLogger(ClassNameUtil.getCurrentClassName());

    private SourcesData sourcesData;
    private Path resultDirectory;

    private IXDocReport ixDocReport;
    private IContext iContext;

    public PdfGenerationTask(SourcesData sourcesData, Path resultDirectory) {
        this.sourcesData = sourcesData;
        this.resultDirectory = resultDirectory;
    }

    @Override
    public PdfGenerationResult call() throws Exception {
        JsonParser parser = new JsonParser();
        String json = new String(sourcesData.getData(), Charset.forName("UTF-8"));
        JsonElement data = parser.parse(json);
        if (!data.isJsonObject()) {
            throw new IllegalStateException("Data is not valid json");
        }

        Path result = resultDirectory.resolve(sourcesData.getTaskId() + ".pdf");
        initReportAndContext();
        Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.ODFDOM);
        fillContext(data);
        OutputStream outputStream = Files.newOutputStream(result);
        ixDocReport.convert(iContext, options, outputStream);
        outputStream.close();
        return new PdfGenerationResult(PdfCreationStatus.DONE);
    }

    private void initReportAndContext() throws XDocReportException, IOException {
        ixDocReport = XDocReportRegistry.getRegistry().loadReport(
                new ByteArrayInputStream(sourcesData.getTemplate()),
                TemplateEngineKind.Freemarker);
        iContext = ixDocReport.createContext();
    }

    private void fillContext(JsonElement data) throws NotFoundException, CannotCompileException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, XDocReportException {
        for (Map.Entry<String, JsonElement> entry : data.getAsJsonObject().entrySet()) {
            if (entry.getValue().isJsonObject()) {
                addTableRows(entry.getValue());
            } else {
                logger.debug("key: {}, value: {}", entry.getKey(), entry.getValue());
                iContext.put(entry.getKey(), entry.getValue().isJsonNull() ? "" : entry.getValue().getAsString());
            }
        }
    }

    private void addTableRows(JsonElement rowsElement) throws XDocReportException, NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        JsonObject jo = rowsElement.getAsJsonObject();
        if (jo.get("rows").isJsonArray()) {
            String rowKey = jo.get("key").getAsString();
            JsonArray rows = jo.get("rows").getAsJsonArray();

            if (rows.size() > 0) {
                final Map<String, Class<?>> properties = new HashMap<>();
                for (Map.Entry<String, JsonElement> rowData : rows.get(0).getAsJsonObject().entrySet()) {
                    logger.debug("{}: {}", rowData.getKey(), rowData.getValue());
                    properties.put(rowData.getKey(), String.class);
                }
                String className = UUID.randomUUID().toString().replaceAll("-", "");
                Class<?> clazz = PojoGenerator.generate(className, properties);
                FieldsMetadata fieldsMetadata = ixDocReport.createFieldsMetadata();
                fieldsMetadata.load(rowKey, clazz, true);
                List rowList = new ArrayList();

                for (JsonElement row : rows) {
                    Object obj = clazz.newInstance();
                    for (Map.Entry<String, JsonElement> rowData : row.getAsJsonObject().entrySet()) {
                        if (rowData.getValue().isJsonArray()) {
                            if (rowData.getValue().isJsonNull()) {
                                clazz.getMethod("set" + Character.toUpperCase(rowData.getKey().charAt(0)) + rowData.getKey().substring(1), String.class)
                                        .invoke(obj, rowData.getValue().isJsonNull() ? "" : rowData.getValue().getAsString());
                            } else {
                                JsonElement value = rowData.getValue();
                                JsonArray asJsonArray = value.getAsJsonArray();
                                final Map<String, Class<?>> itemProperties = new HashMap<>();
                                for (Map.Entry<String, JsonElement> item : asJsonArray.get(0).getAsJsonObject().entrySet()) {
                                    itemProperties.put(item.getKey(), String.class);
                                }
                                String itemClassName = UUID.randomUUID().toString().replaceAll("-", "");
                                Class<?> itemClass = PojoGenerator.generate(itemClassName, itemProperties);
                                ArrayList itemList = new ArrayList();
                                for (JsonElement itemElement : asJsonArray) {
                                    Object itemObj = itemClass.newInstance();
                                    for (Map.Entry<String, JsonElement> itemData : itemElement.getAsJsonObject().entrySet()) {
                                        itemClass.getMethod("set" + Character.toUpperCase(itemData.getKey().charAt(0)) + itemData.getKey().substring(1), String.class)
                                                .invoke(itemObj, itemData.getValue().isJsonNull() ? "" : itemData.getValue().getAsString());
                                    }
                                    itemList.add(itemObj);
                                }
                                clazz.getMethod("set" + Character.toUpperCase(rowData.getKey().charAt(0)) + rowData.getKey().substring(1), List.class)
                                        .invoke(obj, rowData.getValue().isJsonNull() ? new ArrayList() : itemList);
                            }
                        } else {
                            clazz.getMethod("set" + Character.toUpperCase(rowData.getKey().charAt(0)) + rowData.getKey().substring(1), String.class)
                                    .invoke(obj, rowData.getValue().isJsonNull() ? "" : rowData.getValue().getAsString());
                        }
                    }
                    rowList.add(obj);
                }
                iContext.put(rowKey, rowList);
            } else {
                FieldsMetadata fieldsMetadata = ixDocReport.createFieldsMetadata();
                fieldsMetadata.load(rowKey, String.class, true);
                iContext.put(rowKey, new ArrayList());
            }
        } else {
            throw new IllegalStateException("object: \n" + jo.toString() + "\n must have an array named 'rows'");
        }
    }

    public SourcesData getSourcesData() {
        return sourcesData;
    }
}
