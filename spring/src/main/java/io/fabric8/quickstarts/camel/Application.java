/*
 * Copyright 2005-2016 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package io.fabric8.quickstarts.camel;

import java.util.ArrayList;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.util.jsse.KeyManagersParameters;
import org.apache.camel.util.jsse.KeyStoreParameters;
import org.apache.camel.util.jsse.SSLContextParameters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.fabric8.quickstarts.camel.pojo.Root;

import org.apache.camel.util.jsse.SSLContextParameters;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

@SpringBootApplication
@ImportResource({"classpath:spring/camel-context.xml"})
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean servlet = new ServletRegistrationBean(
            new CamelHttpTransportServlet(), "/camel-rest-sql/*");
        servlet.setName("CamelServlet");
        return servlet;
    }

    @Component
    class RestApi extends RouteBuilder {

        @Override
        public void configure() {
        	
        	
            restConfiguration()
                .contextPath("/camel-rest-sql").apiContextPath("/api-doc")
                    .apiProperty("api.title", "Camel REST API")
                    .apiProperty("api.version", "1.0")
                    .apiProperty("cors", "true")
                    .apiContextRouteId("doc-api")
                .component("servlet")
                .bindingMode(RestBindingMode.json);

            rest("/").description("Exhange Rate REST service")
                .get("exchangerate/{currency}/{from_date}/{to_date}").description("The exchange for specified currency")
                    .route().routeId("exchange-rate-api")
                    .to("sql:select exchange_rate from Currency where currency =:#${header.currency} and from_date=:#${header.from_date} and to_date=:#${header.to_date}?" +
                        "dataSource=dataSource&" +
                        "outputClass=io.fabric8.quickstarts.camel.Currency")
                    .endRest()
                .post("exchangerate/insert/{currency}/{exchange_rate}/{from_date}/{to_date}/{description}").description("insert The exchange for specified currency")
                    .route().routeId("insert-exchange-rate-api")
                    .setHeader("CamelSqlRetrieveGeneratedKeys",simple("true", Boolean.class))

                    .to("sql:insert into Currency (id,Currency,Exchange_rate,from_date,to_date,description) values " +
                            "(:#${header.id} , :#${header.currency},:#${header.exchange_rate}, :#${header.from_date},:#${header.to_date}, :#${header.description})?" +
                            "dataSource=dataSource")
                    .log("all headers"+"${header.CamelSqlGeneratedKeyRows}"+"again"+"${header.CamelSqlGeneratedKeysRowCount}")
                   
                    .log("Inserted new order ${header.CamelSqlGeneratedKeyRows}")
                    .setBody(simple("${header.CamelSqlGeneratedKeyRows}"))
                    .endRest()
                .post("payment/").description("insert The payment for specified invoice")
                    .route().routeId("insert-payment-rate-api")
                    .setHeader("CamelSqlRetrieveGeneratedKeys",simple("true", Boolean.class))

                    .log("${body}")
                    .marshal().json(JsonLibrary.Jackson,Payment.class)
                    .log("${body}")
                    .unmarshal().json(JsonLibrary.Jackson,Payment.class)
                    .log("${body}")
                    .log("${body.getPayment_amount}")

                    .to("sql:insert into payment (operator_id,invoice_record,ref_invoice,currency,payment_amount,status,payment_type_id,payment_date,log_date,username) values " +
                            "(:#${body.getOperator_id} , :#${body.getInvoice_record},:#${body.getRef_invoice},:#${body.getCurrency},:#${body.getPayment_amount},:#${body.getStatus}, :#${body.getPayment_type_id},:#${body.getPayment_date},:#${body.getLog_date},:#${body.getUsername})?" +
                            "dataSource=dataSource")
                    
                    .setBody(simple("${header.CamelSqlGeneratedKeyRows}"))
                    .endRest() 
                .get("QueryPayment/{operator_id}/{ref_invoice}").description("Query The payment for specified operator & invoice")
                    .route().routeId("Query-payment-rate-api-opperinvoice")
                    .to("sql:select * from Payment where operator_id =:#${header.operator_id} and ref_invoice=:#${header.ref_invoice}?" +
                            "dataSource=dataSource&" +
                            "outputClass=io.fabric8.quickstarts.camel.Payment")
                    .endRest()    
                .get("QueryPayments/{op_code}/").description("Query The payment for specified operator")
                    .route().routeId("Query-payment-rate-api-opcode")
                    .to("sql:select * from Payment where op_code =:#${header.op_code}?" +
                            "dataSource=dataSource&" +
                            "outputClass=io.fabric8.quickstarts.camel.Payment")
                    .endRest()
                    
                .get("op-configuration/{op_code}/{currency}/{coe_ratio}/{op_tax}/{op_pay_type}").description("Query The payment for specified operator")
                    .route().routeId("operator-configuration-insertion--api-opcode")
                    .to("sql:insert into op_config (op_code,Currency,coe_ratio,op_tax,op_pay_type) values " +
                            "(:#${header.op_code} , :#${header.currency},:#${header.coe_ratio},:#${header.op_tax}, :#${header.op_pay_type})?" +
                            "dataSource=dataSource")
                    .log("genkey"+"${headers}")
                    .setBody(constant("success"))
                    .endRest()
                .get("QueryOpConfig/{op_code}/").description("Query The operator config for specified operator")
                    .route().routeId("operator-config-rate-api-opcode")
                    .to("sql:select * from op_config where op_code =:#${header.op_code}?" +
                            "dataSource=dataSource&" +
                            "outputClass=io.fabric8.quickstarts.camel.Opconfig")
                    .endRest();
                
        }
    }	

    @Component
    class Backend extends RouteBuilder {

        @Override
        public void configure() {
        	JacksonDataFormat df = new JacksonDataFormat(Root.class);
            df.disableFeature(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            df.disableFeature(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
            
            
        	//df.setUseList(true);

        	
            // A first route generates some orders and queue them in DB
            from("sql:select * from bill_cycle where is_predicted = 'no'?" +
                    "consumer.onConsume=update bill_cycle set is_predicted = 'yes' where id = :#tid&" +
                    "consumer.delay={{quickstart.processOrderPeriod:5s}}&" +
                    "dataSource=dataSource&"+"outputClass=io.fabric8.quickstarts.camel.BillCycle")
                .routeId("processing-prediction-for-billcycle").log("${body.id}")
                //.setHeader("CamelSqlRetrieveGeneratedKeys",simple("true", Boolean.class))
                .setHeader("tid",simple("${body.id}"))
                .setHeader("QueryResult",simple("${body}"))
          //      .bean("orderService", "generateOrder")
                .setHeader("Content-Type", constant("application/x-www-form-urlencoded"))
                .setHeader("Accept", constant("application/x-www-form-urlencoded"))
                .setHeader(Exchange.HTTP_METHOD, constant("POST")) 
            

                .to("log:DEBUG?showBody=true&showHeaders=true")
                .setBody(constant("username=admin&password=Oa03216287@"))
                .log("${body}")
                .to("http://localhost:8089/services/auth/login")
                .split().xpath("//response/sessionKey/text()")
                .log("Inserted new order ${body}")
                .setHeader("Content-Type", constant("application/x-www-form-urlencoded"))
                .setHeader("Accept", constant("application/x-www-form-urlencoded"))
                .setHeader(Exchange.HTTP_METHOD, constant("POST")) 
                .setHeader("Authorization",simple("Splunk ${body}"))
                .log("${header.Authorization}")
                .setBody(constant("search=search%20index%3Dinvoices%20%7C%20eval%20_time%3Dstrptime(from_date%2C%20%22%25Y-%25m-%25d%22)%20%7C%20timechart%20span%3D1mon%20values(invoice_amount)%20as%20invoice_amount%20%7C%20predict%20%22invoice_amount%22%20as%20prediction%20algorithm%3DLLP5%20holdback%3D0%20future_timespan%3D5%20upper0%3Dupper0%20lower0%3Dlower0%20%7C%20%60forecastviz(5%2C%200%2C%20%22invoice_amount%22%2C%200)%60"))
            .log("${body}")
            .to("http://localhost:8089/servicesNS/admin/Splunk_ML_Toolkit/search/jobs")
            .split().xpath("//response/sid/text()")
            .log("Inserted new sid ${body}")
            .setHeader("sid",simple("${body}"))
            .setHeader("Content-Type", constant("application/x-www-form-urlencoded"))
            .setHeader("Accept", constant("application/x-www-form-urlencoded"))
            .setHeader(Exchange.HTTP_METHOD, constant("GET")) 
            .delay(10000)
         //   .setBody(constant("output_mode=json"))
            .toD("http://localhost:8089/servicesNS/admin/Splunk_ML_Toolkit/search/jobs/${header.sid}/results/?output_mode=json")
          //  .log("Inserted new predicted search result ${body}") 
            .unmarshal(df)
            
            .process(new Processor() {
                public void process(Exchange exchange) throws Exception {
                    Root output = (Root)exchange.getIn().getBody(Root.class);
                    BillCycle predictdate = (BillCycle)exchange.getIn().getHeader("QueryResult");
                          
//                    writeScript(script,host);
                    for (int i=0;i<output.getResults().size();i++) {
                   
                    	if(output.getResults().get(i).get_time().toString().equals(predictdate.getPredict_start_date().toString()))
                    	{
                    		log.info("result"+output.getResults().get(i).get_time().toString()+"prediction"+output.getResults().get(i).getPrediction()+"span"+output.getResults().get(i).get_spandays());
                            exchange.getIn().setHeader("_time",predictdate.getPredict_start_date());
                            exchange.getIn().setHeader("prediction",Double.parseDouble(output.getResults().get(i).getPrediction()));
                            if(output.getResults().get(i).get_spandays()!=null)
                            {
                                exchange.getIn().setHeader("future_span", Integer.parseInt(output.getResults().get(i).get_spandays()));

	
                            }else exchange.getIn().setHeader("future_span", Integer.parseInt("0"));


                    		break;	
                    	}
                    	
                    	
                    
                    }
//}
                    exchange.getIn().setHeader("op_code",predictdate.getOperator_id());

                    
               }
            })
            .to("sql:insert into predict_revenue (id,operator_id,from_date,to_date,future_span,predicted_amount,username) values " +
                    "(1,:#${header.op_code} , :#${header._time},sysdate, :#${header.future_span}, :#${header.prediction},'atiato')?" +
                    "dataSource=dataSource")
            
            .log("inserted time ${header._time}")

            ;


        //     A second route polls the DB for new orders and processes them
            from("sql:select * from invoices where is_approved = 'no'?" +
                "consumer.onConsume=update invoices set is_approved = 'yes' where id = :#id&" +
                "consumer.delay={{quickstart.processOrderPeriod:5s}}&" +
                "dataSource=dataSource")
                .routeId("generate-csv-for-indexing-in-splunk")
                //.log("Processed order #id ${body.id}")
                .marshal().csv() 
                .to("file:target/reports/?fileName=report.txt&fileExist=Append");
            
            
            
            from("timer:new-order?delay=1s&period={{quickstart.generateOrderPeriod:2s}}")
            .routeId("atiato-proocedure")
            .to("sql-stored:tryitfromcamel(OUT DOUBLE span_out)")
            .to("log:DEBUG?showBody=true&showHeaders=true")
            .process(new Processor() {
                public void process(Exchange exchange) throws Exception {
                	Map results = exchange.getIn().getBody(Map.class);
                	exchange.getIn().setHeader("span_out", results.get("span_out"));
                    
               }
            })
            .delay(10000)
            .log("Inserted new sid ${body}")
            .log("Inserted new sid ${body}")

            .log("Inserted out of proecedure order ${header.span_out}");

        }
    }
}
