package com.alex.edisoft.routes;

import com.alex.edisoft.routes.processor.SaveInStoreProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Роут для обработки файла из директории
 * @author gusev.aleksandr
 * @since 19.02.20
 */
@Component("ftsSiteRoute")
public class FileRoute extends RouteBuilder {

    private SaveInStoreProcessor saveInStoreProcessor;

    public FileRoute(CamelContext context, SaveInStoreProcessor saveInStoreProcessor) {
        super(context);
        this.saveInStoreProcessor = saveInStoreProcessor;
    }

    @Override
    public void configure() throws Exception {
        from("file:{{edisoft.test.directory.path}}").routeId("fileIn")
                .to("xslt:com/alex/edisoft/xslt/idoc2order.xsl")
                .process(saveInStoreProcessor)
                .end();
    }
}