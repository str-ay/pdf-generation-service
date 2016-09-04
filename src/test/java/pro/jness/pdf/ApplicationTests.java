package pro.jness.pdf;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pro.jness.pdf.utils.PdfCreationStatus;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    /**
     * Passed
     */
    @Test
    @Ignore
    public void stress() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        MockMultipartFile firstFile = new MockMultipartFile("data", "testData.json", "application/json",
                Files.readAllBytes(Paths.get(classLoader.getResource("testData.json").toURI())));
        MockMultipartFile secondFile = new MockMultipartFile("template", "testTemplate.odt", "application/octet-stream",
                Files.readAllBytes(Paths.get(classLoader.getResource("testTemplate.odt").toURI())));

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        for (int i = 0; i < 1000; i++) {
            mockMvc.perform(MockMvcRequestBuilders.fileUpload("/task/make")
                    .file(firstFile)
                    .file(secondFile)
            )
                    .andExpect(status().is(200))
                    .andExpect(jsonPath("$.taskId", notNullValue()))
                    .andExpect(jsonPath("$.status", is(PdfCreationStatus.QUEUED.name())))
            ;
        }
        Thread.sleep(100000);
    }

    @Test
    public void makeTask_check_getResult() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        MockMultipartFile firstFile = new MockMultipartFile("data", "testData.json", "application/json",
                Files.readAllBytes(Paths.get(classLoader.getResource("testData.json").toURI())));
        MockMultipartFile secondFile = new MockMultipartFile("template", "testTemplate.odt", "application/octet-stream",
                Files.readAllBytes(Paths.get(classLoader.getResource("testTemplate.odt").toURI())));

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/task/make")
                .file(firstFile)
                .file(secondFile)
        )
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.taskId", notNullValue()))
                .andExpect(jsonPath("$.status", is(PdfCreationStatus.QUEUED.name())))
                .andReturn();
        String resultString = mvcResult.getResponse().getContentAsString();
        JsonParser parser = new JsonParser();
        JsonElement data = parser.parse(resultString);
        String taskId = data.getAsJsonObject().get("taskId").getAsString();

        PdfCreationStatus status;
        MvcResult checkMvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/task/" + taskId + "/check"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.taskId", is(taskId)))
                .andExpect(jsonPath("$.status", isOneOf(PdfCreationStatus.QUEUED.name(), PdfCreationStatus.DONE.name()))).andReturn();
        JsonElement checkResult = parser.parse(checkMvcResult.getResponse().getContentAsString());
        status = PdfCreationStatus.valueOf(checkResult.getAsJsonObject().get("status").getAsString());
        if (!status.equals(PdfCreationStatus.DONE)) {
            for (int i = 0; i < 3; i++) {
                Thread.sleep(1000);
                checkMvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/task/" + taskId + "/check"))
                        .andExpect(status().is(200))
                        .andExpect(jsonPath("$.taskId", is(taskId))).andReturn();
                checkResult = parser.parse(checkMvcResult.getResponse().getContentAsString());
                status = PdfCreationStatus.valueOf(checkResult.getAsJsonObject().get("status").getAsString());
                if (status.equals(PdfCreationStatus.DONE)) {
                    break;
                }
            }
        }
        Assert.assertEquals("Task not completed successfully [" + status + "]", PdfCreationStatus.DONE, status);

        MvcResult getResultMvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/task/" + taskId + "/result"))
                .andExpect(status().is(200))
                .andReturn();
        Assert.assertEquals("Task result is bad. Message: " + getResultMvcResult.getResponse().getContentAsString(), "application/pdf", getResultMvcResult.getResponse().getContentType());
    }
}
