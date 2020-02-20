package com.alex.edisoft.routes;

import com.alex.edisoft.EdisoftTestApplication;
import com.alex.edisoft.model.FileEntity;
import com.alex.edisoft.repositories.FileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * @author gusev.aleksandr
 * @since 19.02.20
 */
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = EdisoftTestApplication.class)
public class FileRouteTest {

    private static final String ORIGINAL_ORDER_XML = "/com/alex/edisoft/routes/original_order.xml";
    private static final String RESULT_XML = "/com/alex/edisoft/routes/out_example.xml";
    private static final String TEST_FILE_NAME = "test.xml";
    private static final String REQUEST_URI = "/files/all";

    @Produce(uri = "file:{{edisoft.test.directory.path}}")
    private ProducerTemplate fileTemplate;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @DirtiesContext
    @Test
    public void camelTest() throws InterruptedException, IOException {
        byte[] originalXmlBody = getResourceBody(ORIGINAL_ORDER_XML);
        fileTemplate.sendBodyAndHeader(originalXmlBody, Exchange.FILE_NAME, TEST_FILE_NAME);

        Thread.sleep(1000);

        Iterable<FileEntity> files = fileRepository.findAll();
        Iterator<FileEntity> filesIterator = files.iterator();

        Assert.assertNotNull(files);
        Assert.assertTrue(filesIterator.hasNext());

        FileEntity entity = filesIterator.next();
        Assert.assertEquals(new String(entity.getSourceFileBody(), StandardCharsets.UTF_8), new String(originalXmlBody, StandardCharsets.UTF_8));

        byte[] resultXmlBody = getResourceBody(RESULT_XML);
        Assert.assertEquals(new String(entity.getFileBody(), StandardCharsets.UTF_8), new String(resultXmlBody, StandardCharsets.UTF_8));
    }

    @DirtiesContext
    @Test
    public void controllerTest() throws Exception {
        byte[] originalXmlBody = getResourceBody(ORIGINAL_ORDER_XML);
        fileTemplate.sendBodyAndHeader(originalXmlBody, Exchange.FILE_NAME, TEST_FILE_NAME);

        Thread.sleep(1000);

        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(REQUEST_URI)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();

        FileEntity[] fileEntities = new ObjectMapper().readValue(content, FileEntity[].class);
        Assert.assertTrue(fileEntities.length > 0);
        Assert.assertEquals(TEST_FILE_NAME, fileEntities[0].getFileName());
    }

    private byte[] getResourceBody(String resourcePath) throws InterruptedException, IOException {
        byte[] resultXmlBody = new byte[0];
        try (InputStream resultXmlIs = FileRouteTest.class.getResource(resourcePath).openStream()) {
            resultXmlBody = new byte[resultXmlIs.available()];
            IOUtils.read(resultXmlIs, resultXmlBody);
        }

        return resultXmlBody;
    }
}
